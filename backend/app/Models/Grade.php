<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Grade extends Model
{
    protected $fillable = ['record_id', 'evaluacion', 'nota', 'porcentaje'];

    public function record() {
        return $this->belongsTo(Record::class);
    }
}
