<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PracticalEvaluation;
use App\Models\PracticalEvaluationCompletion;
use Illuminate\Support\Facades\DB;
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
            ->map(function (PracticalEvaluation $evaluation) use ($user) {
                return $this->formatEvaluation($evaluation, $user, false);
            });

        return response()->json($evaluaciones);
    }

    public function show(Request $request, int $evaluationId)
    {
        $user = $request->user();
        $evaluation = PracticalEvaluation::with([
            'creador:id,name',
            'completions' => function ($query) use ($user) {
                $query->where('user_id', $user->id);
            },
        ])->findOrFail($evaluationId);

        if (!$user->materias()->where('materia_id', $evaluation->materia_id)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a esta evaluación',
            ], 403);
        }

        return response()->json($this->formatEvaluation($evaluation, $user, $evaluation->user_id === $user->id));
    }

    public function store(Request $request, int $materiaId)
    {
        $validated = $request->validate([
            'titulo' => 'required|string|max:255',
            'descripcion' => 'required|string',
            'preguntas' => 'required|array|min:1',
            'preguntas.*.enunciado' => 'required|string',
            'preguntas.*.alternativas' => 'required|array|min:2',
            'preguntas.*.alternativas.*.texto' => 'required|string',
            'preguntas.*.alternativas.*.correcta' => 'required|boolean',
        ]);

        $user = $request->user();

        if (!$user->materias()->where('materia_id', $materiaId)->exists()) {
            return response()->json([
                'message' => 'No puedes crear una evaluación en una materia que no cursas',
            ], 403);
        }

        $preguntas = $validated['preguntas'];
        $contenido = [];

        foreach ($preguntas as $preguntaIndex => $pregunta) {
            $alternativas = collect($pregunta['alternativas']);
            $correctas = $alternativas->where('correcta', true)->count();

            if ($correctas !== 1) {
                return response()->json([
                    'message' => 'Cada pregunta debe tener exactamente una alternativa correcta',
                    'pregunta' => $preguntaIndex,
                ], 422);
            }

            $contenido[] = [
                'enunciado' => $pregunta['enunciado'],
                'alternativas' => $alternativas->map(function (array $alternativa) {
                    return [
                        'texto' => $alternativa['texto'],
                        'correcta' => (bool) $alternativa['correcta'],
                    ];
                })->values()->all(),
            ];
        }

        $evaluation = DB::transaction(function () use ($materiaId, $user, $contenido, $validated) {
            return PracticalEvaluation::create([
                'materia_id' => $materiaId,
                'user_id' => $user->id,
                'titulo' => $validated['titulo'],
                'descripcion' => $validated['descripcion'],
                'pdf_path' => '',
                'tipo' => 'quiz',
                'contenido' => $contenido,
            ]);
        });

        return response()->json(
            $this->formatEvaluation(
                $evaluation->load('creador:id,name'),
                $user,
                true
            ),
            201
        );
    }

    public function download(Request $request, int $evaluationId)
    {
        $user = $request->user();
        $evaluation = PracticalEvaluation::with(['creador:id,name'])->findOrFail($evaluationId);

        if (!$user->materias()->where('materia_id', $evaluation->materia_id)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a esta evaluación',
            ], 403);
        }

        if ($evaluation->tipo === 'quiz') {
            return response()->json($this->formatEvaluation($evaluation, $user, $evaluation->user_id === $user->id));
        }

        if (!$evaluation->pdf_path || !Storage::disk('public')->exists($evaluation->pdf_path)) {
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

        if ($evaluation->tipo === 'quiz') {
            $request->validate([
                'respuestas' => 'required|array',
            ]);

            $respuestas = $request->input('respuestas', []);
            $preguntas = $this->normalizeQuestions($evaluation->contenido ?? []);
            [$correctas, $totalPreguntas, $puntaje, $nota] = $this->calcularResultado($preguntas, $respuestas);

            $completion = PracticalEvaluationCompletion::create([
                'practical_evaluation_id' => $evaluation->id,
                'user_id' => $user->id,
                'completed_at' => now(),
                'nota' => $nota,
                'puntaje' => $puntaje,
                'correctas' => $correctas,
                'total_preguntas' => $totalPreguntas,
                'respuestas' => $this->normalizeResponses($respuestas),
            ]);

            return response()->json([
                'message' => 'Evaluación enviada correctamente',
                'completion' => $this->formatCompletion($completion),
                'evaluation' => $this->formatEvaluation($evaluation->load('creador:id,name'), $user, false),
            ], 201);
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

        if ($evaluation->tipo !== 'quiz' && $evaluation->pdf_path) {
            Storage::disk('public')->delete($evaluation->pdf_path);
        }

        $evaluation->delete();

        return response()->json(['message' => 'Evaluación eliminada correctamente']);
    }

    private function formatEvaluation(PracticalEvaluation $evaluation, $user, bool $includeCorrectAnswers): array
    {
        $completion = $evaluation->completions->first();
        $esCreador = $evaluation->user_id === $user->id;
        $yaCompletada = $completion !== null;

        return [
            'id' => $evaluation->id,
            'materia_id' => $evaluation->materia_id,
            'user_id' => $evaluation->user_id,
            'titulo' => $evaluation->titulo,
            'descripcion' => $evaluation->descripcion,
            'tipo' => $evaluation->tipo,
            'pdf_path' => $evaluation->tipo === 'pdf' ? $evaluation->pdf_path : null,
            'contenido' => $evaluation->tipo === 'quiz'
                ? $this->serializeQuestions($evaluation->contenido ?? [], $includeCorrectAnswers || $esCreador || $yaCompletada)
                : null,
            'creador' => $evaluation->creador,
            'hecha' => $completion !== null,
            'nota' => $completion?->nota,
            'puntaje' => $completion?->puntaje,
            'correctas' => $completion?->correctas,
            'total_preguntas' => $completion?->total_preguntas,
            'completed_at' => $completion?->completed_at,
            'completion' => $completion ? $this->formatCompletion($completion) : null,
            'completions' => $evaluation->completions->map(function (PracticalEvaluationCompletion $item) {
                return $this->formatCompletion($item);
            })->values()->all(),
            'created_at' => $evaluation->created_at,
            'updated_at' => $evaluation->updated_at,
        ];
    }

    private function serializeQuestions(array $preguntas, bool $includeCorrectAnswers): array
    {
        return collect($preguntas)->map(function (array $pregunta) use ($includeCorrectAnswers) {
            return [
                'enunciado' => $pregunta['enunciado'] ?? '',
                'alternativas' => collect($pregunta['alternativas'] ?? [])->map(function (array $alternativa) use ($includeCorrectAnswers) {
                    $item = [
                        'texto' => $alternativa['texto'] ?? '',
                    ];

                    if ($includeCorrectAnswers) {
                        $item['correcta'] = (bool) ($alternativa['correcta'] ?? false);
                    }

                    return $item;
                })->values()->all(),
            ];
        })->values()->all();
    }

    private function normalizeQuestions(array $preguntas): array
    {
        return collect($preguntas)->map(function (array $pregunta) {
            return [
                'enunciado' => $pregunta['enunciado'] ?? '',
                'alternativas' => collect($pregunta['alternativas'] ?? [])->map(function (array $alternativa) {
                    return [
                        'texto' => $alternativa['texto'] ?? '',
                        'correcta' => (bool) ($alternativa['correcta'] ?? false),
                    ];
                })->values()->all(),
            ];
        })->values()->all();
    }

    private function normalizeResponses(array $respuestas): array
    {
        return collect($respuestas)->map(function ($respuesta) {
            return is_numeric($respuesta) ? (int) $respuesta : $respuesta;
        })->values()->all();
    }

    private function calcularResultado(array $preguntas, array $respuestas): array
    {
        $totalPreguntas = count($preguntas);
        $correctas = 0;

        foreach ($preguntas as $indicePregunta => $pregunta) {
            $respuestaUsuario = $respuestas[$indicePregunta] ?? null;
            $alternativas = $pregunta['alternativas'] ?? [];

            if ($respuestaUsuario === null || !isset($alternativas[(int) $respuestaUsuario])) {
                continue;
            }

            if (!empty($alternativas[(int) $respuestaUsuario]['correcta'])) {
                $correctas++;
            }
        }

        $puntaje = $totalPreguntas > 0 ? round(($correctas / $totalPreguntas) * 100, 2) : 0.0;
        $nota = $totalPreguntas > 0
            ? round(1 + (6 * ($correctas / $totalPreguntas)), 1)
            : 1.0;

        return [$correctas, $totalPreguntas, $puntaje, $nota];
    }

    private function formatCompletion(PracticalEvaluationCompletion $completion): array
    {
        return [
            'id' => $completion->id,
            'practical_evaluation_id' => $completion->practical_evaluation_id,
            'user_id' => $completion->user_id,
            'nota' => $completion->nota,
            'puntaje' => $completion->puntaje,
            'correctas' => $completion->correctas,
            'total_preguntas' => $completion->total_preguntas,
            'completed_at' => $completion->completed_at,
            'respuestas' => $completion->respuestas,
            'created_at' => $completion->created_at,
            'updated_at' => $completion->updated_at,
        ];
    }
}