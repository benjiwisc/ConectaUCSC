<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SesionMensaje extends Model
{
    protected $fillable = [
        'sesion_estudio_id',
        'user_id',
        'message',
    ];

    public function sesionEstudio()
    {
        return $this->belongsTo(SesionEstudio::class);
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}