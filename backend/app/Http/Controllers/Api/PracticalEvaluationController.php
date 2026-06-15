<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PracticalEvaluation;
use App\Models\PracticalEvaluationCompletion;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class PracticalEvaluationController extends Controller
{
    public function porMateria(Request $request, int $materiaId)
    {
        $user = $request->user();

        if (!$user->materias()->where('materia_id', $materiaId)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a las evaluaciones de esta materia',
            ], 403);
        }

        $evaluaciones = PracticalEvaluation::with([
            'creador:id,name',
            'completions' => function ($query) use ($user) {
                $query->where('user_id', $user->id);
            },
        ])
            ->where('materia_id', $materiaId)
            ->orderByDesc('created_at')
            ->get()
            ->map(function (PracticalEvaluation $evaluation) {
                return [
                    'id' => $evaluation->id,
                    'materia_id' => $evaluation->materia_id,
                    'user_id' => $evaluation->user_id,
                    'titulo' => $evaluation->titulo,
                    'descripcion' => $evaluation->descripcion,
                    'pdf_path' => $evaluation->pdf_path,
                    'creador' => $evaluation->creador,
                    'hecha' => $evaluation->completions->isNotEmpty(),
                    'created_at' => $evaluation->created_at,
                    'updated_at' => $evaluation->updated_at,
                ];
            });

        return response()->json($evaluaciones);
    }

    public function store(Request $request, int $materiaId)
    {
        $request->validate([
            'titulo' => 'required|string|max:255',
            'descripcion' => 'required|string',
            'pdf' => 'required|file|mimes:pdf|max:20480',
        ]);

        $user = $request->user();

        if (!$user->materias()->where('materia_id', $materiaId)->exists()) {
            return response()->json([
                'message' => 'No puedes crear una evaluación en una materia que no cursas',
            ], 403);
        }

        $pdfPath = $request->file('pdf')->store('practical-evaluations', 'public');

        $evaluation = PracticalEvaluation::create([
            'materia_id' => $materiaId,
            'user_id' => $user->id,
            'titulo' => $request->titulo,
            'descripcion' => $request->descripcion,
            'pdf_path' => $pdfPath,
        ]);

        return response()->json($evaluation->load('creador:id,name'), 201);
    }

    public function download(Request $request, int $evaluationId)
    {
        $user = $request->user();
        $evaluation = PracticalEvaluation::findOrFail($evaluationId);

        if (!$user->materias()->where('materia_id', $evaluation->materia_id)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a esta evaluación',
            ], 403);
        }

        if (!Storage::disk('public')->exists($evaluation->pdf_path)) {
            return response()->json([
                'message' => 'El archivo de la evaluación no está disponible',
            ], 404);
        }

        $fileName = $evaluation->titulo . '.pdf';

        return Storage::disk('public')->download($evaluation->pdf_path, $fileName);
    }

    public function marcarHecha(Request $request, int $evaluationId)
    {
        $user = $request->user();
        $evaluation = PracticalEvaluation::findOrFail($evaluationId);

        if (!$user->materias()->where('materia_id', $evaluation->materia_id)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a esta evaluación',
            ], 403);
        }

        $alreadyCompleted = PracticalEvaluationCompletion::where('practical_evaluation_id', $evaluation->id)
            ->where('user_id', $user->id)
            ->exists();

        if ($alreadyCompleted) {
            return response()->json([
                'message' => 'Ya marcaste esta evaluación como hecha',
            ], 409);
        }

        $completion = PracticalEvaluationCompletion::create([
            'practical_evaluation_id' => $evaluation->id,
            'user_id' => $user->id,
            'completed_at' => now(),
        ]);

        return response()->json($completion, 201);
    }

    public function destroy(Request $request, int $evaluationId)
    {
        $user = $request->user();
        $evaluation = PracticalEvaluation::findOrFail($evaluationId);

        if ($evaluation->user_id !== $user->id) {
            return response()->json([
                'message' => 'Solo el creador puede eliminar esta evaluación',
            ], 403);
        }

        if ($evaluation->pdf_path) {
            Storage::disk('public')->delete($evaluation->pdf_path);
        }

        $evaluation->delete();

        return response()->json(['message' => 'Evaluación eliminada correctamente']);
    }
}