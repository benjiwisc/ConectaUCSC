<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Materia;
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

        $user->materias()->attach($request->materia_id);

        return response()->json(['message' => 'Materia agregada correctamente']);
    }

    // Eliminar materia del usuario
    public function eliminar(Request $request, $materiaId)
    {
        $request->user()->materias()->detach($materiaId);
        return response()->json(['message' => 'Materia eliminada correctamente']);
    }
}