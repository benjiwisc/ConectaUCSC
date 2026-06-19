<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    protected $fillable = [
        'name',
        'email',
        'password',
        'facultad_id',
        'carrera_id',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];
    
    public function carrera()
    {
        return $this->belongsTo(Carrera::class);
    }

    public function facultad()
    {
        return $this->belongsTo(Facultad::class);
    }

    public function materias()
    {
        return $this->belongsToMany(Materia::class, 'usuario_materias')->withPivot('id');
    }

    public function practicalEvaluations()
    {
        return $this->hasMany(PracticalEvaluation::class);
    }

    public function practicalEvaluationCompletions()
    {
        return $this->hasMany(PracticalEvaluationCompletion::class);
    }

    public function fileManagementItems()
    {
        return $this->hasMany(FileManagement::class, 'user_id');
    }
}