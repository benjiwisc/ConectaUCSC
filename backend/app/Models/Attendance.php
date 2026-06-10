<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Attendance extends Model
{
    protected $fillable = ['record_id', 'fecha', 'estado'];

    public function record() {
        return $this->belongsTo(Record::class);
    }
}
