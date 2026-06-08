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
    
    public function materias()
    {
        return $this->belongsToMany(Materia::class, 'usuario_materias');
    }
}