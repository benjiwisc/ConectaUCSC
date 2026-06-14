<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Schedule extends Model
{
    protected $fillable = ['id','record_id', 'dia', 'hora_inicio', 'hora_fin', 'sala', 'tipo_clase'];

    public function record() {
        return $this->belongsTo(Record::class);
    }
}
