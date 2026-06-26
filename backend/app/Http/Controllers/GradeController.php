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

    public function edit($Id) {
        return Grade::where('id', $Id)->first();
    }

    public function store(Request $request) {
        $nota = $request->input('nota');
        if ($nota !== null) {
            
            $nota = str_replace(',', '.', $nota);
            
            if (is_numeric($nota) && floatval($nota) > 7.0) {
                $nota = floatval($nota) / 10;
            }
            $request->merge(['nota' => $nota]);
        }

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
        $nota = $request->input('nota');
        if ($nota !== null) {
            
            $nota = str_replace(',', '.', $nota);
            
            if (is_numeric($nota) && floatval($nota) > 7.0) {
                $nota = floatval($nota) / 10;
            }
            $request->merge(['nota' => $nota]);
        }

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
        return response()->json(['message' => 'Nota eliminada correctamente']);
    }

    
    public function promedio($recordId) {
        $promedio = Grade::where('record_id', $recordId)->avg('nota');
        return response()->json(['promedio' => round($promedio, 1)]);
    }

    
    public function notaNecesaria(Request $request, $recordId) {
        $grades = Grade::where('record_id', $recordId)->get();

        $sumaPorcentajes = $grades->sum('porcentaje');
        
        
        $notaAcumulada = $grades->sum(function ($grade) {
            return $grade->nota * ($grade->porcentaje / 100);
        });

        $porcentajeRestante = 100 - $sumaPorcentajes;
        
        $notaAprobacion = 4.0;
        if ($request->has('nota_aprobacion')) {
            $val = floatval($request->query('nota_aprobacion'));
            if ($val >= 1.0 && $val <= 7.0) {
                $notaAprobacion = $val;
            }
        }
        
        $notaNecesaria = 1.0;
        $estado = 'cursando';

        if ($porcentajeRestante <= 0) {
            
            if ($notaAcumulada >= $notaAprobacion) {
                $estado = 'aprobado';
                $notaNecesaria = 1.0;
            } else {
                $estado = 'reprobado';
                $notaNecesaria = null;
            }
        } else {
            
            $calculo = ($notaAprobacion - $notaAcumulada) / ($porcentajeRestante / 100);
            $calculo = round($calculo, 1);

            if ($calculo <= 1.0) {
                $notaNecesaria = 1.0; 
                $estado = 'aprobado'; 
            } elseif ($calculo > 7.0) {
                $notaNecesaria = $calculo; 
                $estado = 'reprobado'; 
            } else {
                $notaNecesaria = $calculo;
                $estado = 'cursando';
            }
        }

        return response()->json([
            'nota_acumulada'      => round($notaAcumulada, 2),
            'porcentaje_acumulado'=> $sumaPorcentajes,
            'porcentaje_restante' => $porcentajeRestante,
            'nota_necesaria'      => $notaNecesaria,
            'estado'              => $estado,
        ]);
    }
}
