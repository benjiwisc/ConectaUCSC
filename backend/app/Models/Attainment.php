<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Attainment extends Model
{
    protected $fillable = [
        'record_id',
        'tipo',
        'meta',
        'progreso',
        'cumplido',
    ];

    public function record() {
        return $this->belongsTo(Record::class);
    }
}
