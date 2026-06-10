<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Schedule;

class ScheduleController extends Controller
{
    public function index($recordId) {
        return Schedule::where('record_id', $recordId)->get();
    }

    public function store(Request $request)
    {
        $request->validate([
            'record_id'   => 'required|exists:records,usuario_materia_id',
            'dia'         => 'required|in:lunes,martes,miercoles,jueves,viernes,sabado',
            'hora_inicio' => 'required|date_format:H:i',
            'hora_fin'    => 'required|date_format:H:i|after:hora_inicio',
            'sala'        => 'nullable|string',
            'tipo_clase'  => 'required|in:laboratorio,ayudantia,catedra'
        ]);

        $cruce = Schedule::where('record_id', $request->record_id)
            ->where('dia', $request->dia)
            ->where(function($q) use ($request) {
                $q->whereBetween('hora_inicio', [$request->hora_inicio, $request->hora_fin])
                ->orWhereBetween('hora_fin', [$request->hora_inicio, $request->hora_fin])
                ->orWhere(function($q) use ($request) {
                    $q->where('hora_inicio', '<=', $request->hora_inicio)
                        ->where('hora_fin', '>=', $request->hora_fin);
                });
            })
            ->exists();

        if ($cruce) {
            return response()->json(['message' => 'Ya tienes una clase en ese horario para este ramo'], 409);
        }

        $schedule = Schedule::create($request->all());
        return response()->json($schedule, 201);
    }

    public function update(Request $request, $id)
    {
        $schedule = Schedule::findOrFail($id);

        $request->validate([
            'dia'         => 'sometimes|in:lunes,martes,miercoles,jueves,viernes,sabado',
            'hora_inicio' => 'sometimes|date_format:H:i',
            'hora_fin'    => 'sometimes|date_format:H:i|after:hora_inicio',
            'sala'        => 'nullable|string',
            'tipo_clase'  => 'sometimes|in:laboratorio,ayudantia,catedra'
        ]);

        // verificar cruce excluyendo el horario actual
        $cruce = Schedule::where('record_id', $schedule->record_id)
            ->where('dia', $request->dia ?? $schedule->dia)
            ->where('id', '!=', $id)
            ->where(function($q) use ($request, $schedule) {
                $horaInicio = $request->hora_inicio ?? $schedule->hora_inicio;
                $horaFin    = $request->hora_fin ?? $schedule->hora_fin;
                $q->whereBetween('hora_inicio', [$horaInicio, $horaFin])
                ->orWhereBetween('hora_fin', [$horaInicio, $horaFin])
                ->orWhere(function($q) use ($horaInicio, $horaFin) {
                    $q->where('hora_inicio', '<=', $horaInicio)
                        ->where('hora_fin', '>=', $horaFin);
                });
            })
            ->exists();

        if ($cruce) {
            return response()->json(['message' => 'Ya tienes una clase en ese horario para este ramo'], 409);
        }

        $schedule->update($request->only('dia', 'hora_inicio', 'hora_fin', 'sala', 'tipo_clase'));
        return response()->json($schedule);
    }

    public function destroy($id) {
        Schedule::findOrFail($id)->delete();
        return response()->json(null, 204);
    }
}
