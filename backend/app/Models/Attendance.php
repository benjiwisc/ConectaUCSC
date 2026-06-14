<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Attendance extends Model
{
    protected $fillable = ['schedule_id','record_id', 'fecha', 'estado'];

    public function record() {
        return $this->belongsTo(Record::class);
    }
}
