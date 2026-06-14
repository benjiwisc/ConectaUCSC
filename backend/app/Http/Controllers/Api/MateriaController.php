<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Materia;
use App\Models\Record;
use App\Models\UsuarioMateria;
use App\Models\Attainment;
use Illuminate\Http\Request;

class MateriaController extends Controller
{
    // Listar materias disponibles por carrera
    public function porCarrera($carreraId)
    {
        $materias = Materia::where('carrera_id', $carreraId)->get();
        return response()->json($materias);
    }

    // Listar materias que cursa el usuario
    public function misMateria(Request $request)
    {
        $materias = $request->user()->materias()->get();
        $materias = $materias->map(function ($materia) {
        return [
            'id'         => $materia->id,  
            'nombre'     => $materia->nombre,
            'carrera_id' => $materia->carrera_id,
            'id_registro'=> $materia->pivot->id
        ];
      
    });

    return response()->json($materias);
        return response()->json($materias);
    }

    // Agregar materia al usuario
    public function agregar(Request $request)
    {
        $request->validate([
            'materia_id' => 'required|exists:materias,id',
        ]);

        $user = $request->user();

        if ($user->materias()->where('materia_id', $request->materia_id)->exists()) {
            return response()->json(['message' => 'Ya estás cursando esta materia'], 409);
        }

        $usuarioMateria = UsuarioMateria::create([
            'user_id' => $user->id,
            'materia_id' => $request->materia_id,
        ]);

        $record = Record::create(['usuario_materia_id' => $usuarioMateria->id]);

        $logros = [
            ['tipo' => 'primer_paso',        'meta' => 1],
            ['tipo' => 'constante',          'meta' => 5],
            ['tipo' => 'maraton',            'meta' => 4],
            ['tipo' => 'presente_siempre',   'meta' => 100],
            ['tipo' => 'compromiso_medido',  'meta' => 75],
            ['tipo' => 'asistencia_ejemplar','meta' => 90],
            ['tipo' => 'leyendo_asistencia', 'meta' => 95],
        ];

        foreach ($logros as $logro) {
            Attainment::create([
                'record_id' => $record->id,
                'tipo'      => $logro['tipo'],
                'meta'      => $logro['meta'],
                'progreso'  => 0,
                'cumplido'  => false,
            ]);
        }

        return response()->json(['message' => 'Materia agregada correctamente']);
    }

    // Eliminar materia del usuario
    public function eliminar(Request $request, $materiaId)
    {
        $request->user()->materias()->detach($materiaId);
        return response()->json(['message' => 'Materia eliminada correctamente']);
    }
}