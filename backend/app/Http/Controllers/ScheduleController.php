<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Schedule;

class ScheduleController extends Controller
{
    public function allSchedules(Request $request)
    {
        $user = $request->user();
        $recordIds = \DB::table('usuario_materias')
            ->join('records', 'usuario_materias.id', '=', 'records.usuario_materia_id')
            ->where('usuario_materias.user_id', $user->id)
            ->pluck('records.id')
            ->toArray();

        $schedules = Schedule::with(['record.usuarioMateria.materia:id,nombre'])
            ->whereIn('record_id', $recordIds)
            ->get();

        return response()->json($schedules);
    }

    public function index($recordId) {
        return Schedule::where('record_id', $recordId)->get();
    }

    public function edit($Id) {
        return Schedule::where('id', $Id)->first();
    }

    public function store(Request $request)
    {
        $request->validate([
            'record_id'   => 'required',
            'dia'         => 'required|in:lunes,martes,miércoles,jueves,viernes,sábado',
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
        return response()->json([
            'success' => true,
            'message' => 'Horario eliminado'
        ]);
    }
}
