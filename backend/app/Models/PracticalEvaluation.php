<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class PracticalEvaluation extends Model
{
    protected $table = 'practical_evaluation';

    protected $fillable = [
        'materia_id',
        'user_id',
        'titulo',
        'descripcion',
        'pdf_path',
        'tipo',
        'contenido',
    ];

    protected $casts = [
        'contenido' => 'array',
    ];

    public function materia()
    {
        return $this->belongsTo(Materia::class);
    }

    public function creador()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function completions()
    {
        return $this->hasMany(PracticalEvaluationCompletion::class);
    }
}