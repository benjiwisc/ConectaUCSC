<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Grade;

class GradeController extends Controller
{
    public function index($recordId) {
        return Grade::where('record_id', $recordId)->get();
    }

    public function store(Request $request) {
        $request->validate([
            'record_id'  => 'required|exists:records,usuario_materia_id',
            'evaluacion' => 'required|string',
            'porcentaje' => 'required|integer',
            'nota'       => 'required|numeric|min:1|max:7',
        ]);
        $grade = Grade::create($request->all());
        return response()->json($grade, 201);
    }

    public function update(Request $request, $id) {
        $grade = Grade::findOrFail($id);
        $request->validate([
            'evaluacion' => 'sometimes|string',
            'porcentaje' => 'sometimes|integer',
            'nota'       => 'sometimes|numeric|min:1|max:7',
        ]);
        $grade->update($request->only('evaluacion', 'nota', 'porcentaje'));
        return response()->json($grade);
    }

    public function destroy($id) {
        Grade::findOrFail($id)->delete();
        return response()->json(null, 204);
    }

    // promedio de notas del record
    public function promedio($recordId) {
        $promedio = Grade::where('record_id', $recordId)->avg('nota');
        return response()->json(['promedio' => round($promedio, 1)]);
    }
}
