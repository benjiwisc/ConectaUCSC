<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class UsuarioMateria extends Model
{
    protected $fillable = ['id','user_id', 'materia_id'];
}