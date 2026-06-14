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

    public function update(Request $request, $id)
    {
        $attendance = Attendance::findOrFail($id);

        $request->validate([
            'estado' => 'required|in:presente,ausente,justificado',
        ]);

        $attendance->update([
            'estado' => $request->estado,
        ]);

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
        return response()->json(null, 204);
    }

    public function registrarPorUbicacion(Request $request)
    {
        $request->validate([
            'latitud'  => 'required|numeric',
            'longitud' => 'required|numeric',
            'fecha'    => 'required|date',
        ]);

        if (!$this->verificarUbicacion($request->latitud, $request->longitud)) {
            return response()->json(['message' => 'Estás fuera del campus'], 409);
        }

        $diaHoy = strtolower(now()->locale('es')->dayName);
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

        $registradas = [];

        foreach ($records as $record) {

            $yaRegistrada = Attendance::where('record_id', $record->id)
                ->whereDate('fecha', $request->fecha)
                ->exists();

            if (!$yaRegistrada) {

                $attendance = Attendance::create([
                    'record_id' => $record->id,
                    'fecha'     => $request->fecha,
                    'estado'    => 'presente',
                ]);

                $registradas[] = $attendance;
                $this->verificarLogros($record);
            }
        }

        return response()->json([
            'message'     => 'Asistencia registrada en todos tus ramos de hoy',
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