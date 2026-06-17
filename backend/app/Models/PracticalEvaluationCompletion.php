<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class PracticalEvaluationCompletion extends Model
{
    protected $fillable = [
        'practical_evaluation_id',
        'user_id',
        'completed_at',
        'nota',
        'puntaje',
        'correctas',
        'total_preguntas',
        'respuestas',
    ];

    protected $casts = [
        'completed_at' => 'datetime',
        'respuestas' => 'array',
    ];

    public function evaluation()
    {
        return $this->belongsTo(PracticalEvaluation::class, 'practical_evaluation_id');
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}