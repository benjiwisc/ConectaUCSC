<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Attendance;
use App\Models\Attainment;
use App\Models\Record;
use App\Http\Controllers\AttainmentController;

class AttendanceController extends Controller
{
    public function index($recordId)
    {
        return Attendance::where('record_id', $recordId)->get();
    }

    public function store(Request $request)
    {
        $request->validate([
            'record_id' => 'required|exists:records,id',
            'fecha'     => 'required|date',
            'estado'    => 'required|in:presente,ausente,justificado',
        ]);

        $attendance = Attendance::create([
            'record_id' => $request->record_id,
            'fecha'     => $request->fecha,
            'estado'    => $request->estado,
        ]);

        return response()->json($attendance, 201);
    }

    public function update($id)
    {
        $attendance = Attendance::findOrFail($id);

        if($attendance->estado == 'presente'){
            $attendance->update([
                'estado' => 'ausente',
            ]);

        }else{
            $attendance->update([
                'estado' => 'presente',
            ]);
        }

        return response()->json($attendance);
    }

    public function justificar($id)
    {
        $attendance = Attendance::findOrFail($id);
        $attendance->update(['cancelado' => true]);

        return response()->json($attendance);
    }


    public function destroy($id)
    {
        Attendance::findOrFail($id)->delete();
        return response()->json([
            'success' => true,
            'message' => 'Asistencia eliminada'
        ]);
    }

   public function registrarPorUbicacion(Request $request)
    {
        $request->validate([
            'latitud'  => 'required|numeric',
            'longitud' => 'required|numeric',
        ]);

        $fechaHoy = now()->toDateString(); 
        $diaHoy = strtolower(now()->locale('es')->dayName);
        $horaActual = now()->format('H:i');
        $usuario = $request->user();

        $records = Record::whereHas('usuarioMateria', function ($q) use ($usuario) {
                $q->where('user_id', $usuario->id);
            })
            ->whereHas('schedules', function ($q) use ($diaHoy) {
                $q->where('dia', $diaHoy);
            })
            ->get();

        if ($records->isEmpty()) {
            return response()->json(['message' => 'No tienes clases hoy'], 409);
        }

        $enCampus = $this->verificarUbicacion($request->latitud, $request->longitud);
        $registradas = [];

        foreach ($records as $record) {

            $bloques = $record->schedules()->where('dia', $diaHoy)->get();
            
            foreach ($bloques as $bloque) {

                $asistencia = Attendance::where('record_id', $record->id)
                    ->where('schedule_id', $bloque->id)
                    ->whereDate('fecha', $fechaHoy)
                    ->first();
                        

                if (!$asistencia) {
                    $asistencia = Attendance::create([
                        'record_id'   => $record->id,
                        'schedule_id' => $bloque->id,
                        'fecha'       => $fechaHoy,
                        'estado'      => 'ausente',
                    ]);
                    
                }

                $enHorario = $horaActual >= date('H:i', strtotime('-30 minutes', strtotime($bloque->hora_inicio)))
                        && $horaActual <= $bloque->hora_fin;

                if ($enCampus && $enHorario && $asistencia->estado !== 'presente') {
                    $asistencia->update(['estado' => 'presente']);
                    $this->verificarLogros($record);
                }

                $registradas[] = $asistencia->fresh();
            }
        }

        $todosPresentes = collect($registradas)->every(fn($a) => $a->estado === 'presente');

        return response()->json([
            'message'     => $todosPresentes
                ? 'Ya tienes asistencia registrada como presente'
                : ($enCampus ? 'Asistencia registrada como presente' : 'No estás en el campus'),
            'registradas' => $registradas
        ], 201);
    }

    private function verificarLogros(Record $record)
    {
        $this->porConteo($record);
        $this->porMes($record);

        if ($record->finalizado) {
            $this->porPorcentaje($record);
        }
    }

    private function porConteo(Record $record)
    {
        $total = $record->attendances()->where('estado', 'presente')->count();

        $this->actualizar('primer_paso', $record, $total);
        $this->actualizar('constante', $record, $total);
    }

    private function porMes(Record $record)
    {
        $semana = $record->attendances()
            ->where('estado', 'presente')
            ->whereBetween('fecha', [now()->startOfWeek(), now()->endOfWeek()])
            ->count();

        $this->actualizar('maraton', $record, $semana);

        $clasesMes = $record->schedules()->count() * 4;

        $asistenciasMes = $record->attendances()
            ->where('estado', 'presente')
            ->whereBetween('fecha', [now()->startOfMonth(), now()->endOfMonth()])
            ->count();

        $porcentaje = $clasesMes > 0 ? ($asistenciasMes / $clasesMes) * 100 : 0;

        $this->actualizar('presente_siempre', $record, $porcentaje);
    }

    private function porPorcentaje(Record $record)
    {
        $total = $record->attendances()->count();
        if ($total === 0) return;

        $presentes = $record->attendances()->where('estado', 'presente')->count();

        $porcentaje = ($presentes / $total) * 100;

        $this->actualizar('compromiso_medido', $record, $porcentaje);
        $this->actualizar('asistencia_ejemplar', $record, $porcentaje);
        $this->actualizar('leyendo_asistencia', $record, $porcentaje);
    }

    private function actualizar($tipo, Record $record, $valor)
    {
        $attainment = Attainment::where('record_id', $record->id)
            ->where('tipo', $tipo)
            ->first();

        if (!$attainment || $attainment->cumplido) return;

        $attainment->progreso = min($valor, $attainment->meta);

        if ($attainment->progreso >= $attainment->meta) {
            $attainment->cumplido = true;
        }

        $attainment->save();
    }

    private function verificarUbicacion($lat, $lon)
    {
        $uniLat = -36.796852393708576;
        $uniLon = -73.05623128084697;
        $radio = 300;

        return $this->calcularDistancia($lat, $lon, $uniLat, $uniLon) <= $radio;
    }

    private function calcularDistancia($lat1, $lon1, $lat2, $lon2)
    {
        $R = 6371000;

        $dLat = deg2rad($lat2 - $lat1);
        $dLon = deg2rad($lon2 - $lon1);

        $a = sin($dLat / 2) ** 2 +
            cos(deg2rad($lat1)) *
            cos(deg2rad($lat2)) *
            sin($dLon / 2) ** 2;

        return $R * 2 * atan2(sqrt($a), sqrt(1 - $a));
    }
}