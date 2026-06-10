<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SesionEstudio extends Model
{
    protected $fillable = [
        'titulo',
        'lugar',
        'fecha_hora',
        'descripcion',
        'materia_id',
        'user_id',
    ];

    public function materia()
    {
        return $this->belongsTo(Materia::class);
    }

    public function creador()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function participantes()
    {
        return $this->belongsToMany(User::class, 'sesion_participantes');
    }
}