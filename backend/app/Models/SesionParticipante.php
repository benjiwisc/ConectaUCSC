<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SesionParticipante extends Model
{
    protected $fillable = ['sesion_estudio_id', 'user_id'];
}