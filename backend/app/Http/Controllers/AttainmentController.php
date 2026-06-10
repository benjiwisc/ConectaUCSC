<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Attainment;
use App\Models\Record;

class AttainmentController extends Controller
{
    public function index($recordId)
    {
        return Attainment::where('record_id', $recordId)->get();
    }

    public function store(Request $request)
    {
        $request->validate([
            'record_id' => 'required|exists:records,id',
            'meta'      => 'required|integer|min:1',
            'tipo'      => 'required|in:primer_paso,constante,maraton,presente_siempre,compromiso_medido,asistencia_ejemplar,leyendo_asistencia',
        ]);

        $attainment = Attainment::create([
            'record_id' => $request->record_id,
            'tipo'      => $request->tipo,
            'meta'      => $request->meta,
            'progreso'  => 0,
            'cumplido'  => false,
        ]);

        return response()->json($attainment, 201);
    }

    public function update(Request $request, $id)
    {
        $attainment = Attainment::findOrFail($id);

        $request->validate([
            'meta' => 'sometimes|integer|min:1',
            'tipo' => 'sometimes|in:primer_paso,constante,maraton,presente_siempre,compromiso_medido,asistencia_ejemplar,leyendo_asistencia',
        ]);

        $attainment->update($request->only('meta', 'tipo'));

        return response()->json($attainment);
    }

    public function destroy($id)
    {
        Attainment::findOrFail($id)->delete();
        return response()->json(null, 204);
    }

    public function verificarLogros(Record $record)
    {
        $this->verificarLogrosPorConteo($record);
        $this->verificarLogrosPorMes($record);

        if ($record->finalizado) {
            $this->verificarLogrosPorcentaje($record);
        }
    }

    private function verificarLogrosPorConteo(Record $record)
    {
        $total = $record->attendances()
            ->where('estado', 'presente')
            ->count();

        $this->actualizarProgreso($record, 'primer_paso', $total);
        $this->actualizarProgreso($record, 'constante', $total);
    }

    private function verificarLogrosPorMes(Record $record)
    {
        $semana = $record->attendances()
            ->where('estado', 'presente')
            ->whereBetween('fecha', [now()->startOfWeek(), now()->endOfWeek()])
            ->count();

        $this->actualizarProgreso($record, 'maraton', $semana);

        $clasesMes = $record->schedules()->count() * 4;

        $asistenciasMes = $record->attendances()
            ->where('estado', 'presente')
            ->whereBetween('fecha', [now()->startOfMonth(), now()->endOfMonth()])
            ->count();

        $porcentajeMes = $clasesMes > 0
            ? ($asistenciasMes / $clasesMes) * 100
            : 0;

        $this->actualizarProgreso($record, 'presente_siempre', $porcentajeMes);
    }

    private function verificarLogrosPorcentaje(Record $record)
    {
        $total = $record->attendances()->count();
        if ($total === 0) return;

        $presentes = $record->attendances()
            ->where('estado', 'presente')
            ->count();

        $porcentaje = ($presentes / $total) * 100;

        $this->actualizarProgreso($record, 'compromiso_medido', $porcentaje);
        $this->actualizarProgreso($record, 'asistencia_ejemplar', $porcentaje);
        $this->actualizarProgreso($record, 'leyendo_asistencia', $porcentaje);
    }

    private function actualizarProgreso(Record $record, string $tipo, float $progreso)
    {
        $attainment = Attainment::where('record_id', $record->id)
            ->where('tipo', $tipo)
            ->first();

        if (!$attainment || $attainment->cumplido) return;

        $attainment->progreso = min($progreso, $attainment->meta);

        if ($attainment->progreso >= $attainment->meta) {
            $attainment->cumplido = true;
        }

        $attainment->save();
    }
}