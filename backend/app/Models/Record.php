<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Record extends Model
{
    protected $fillable = ['usuario_materia_id'];

    public function usuarioMateria() {
        return $this->belongsTo(UsuarioMateria::class);
    }

    public function attendances() {
        return $this->hasMany(Attendance::class);
    }

    public function grades() {
        return $this->hasMany(Grade::class);
    }

    public function attainments() {
        return $this->hasMany(Attainment::class);
    }

    public function schedules() {
        return $this->hasMany(Schedule::class);
    }
}
