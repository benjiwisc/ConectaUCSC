<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class FileManagement extends Model
{
    protected $table = 'file_management';

    protected $fillable = [
        'materia_id',
        'user_id',
        'titulo',
        'descripcion',
        'file_path',
        'file_name',
        'file_extension',
        'mime_type',
    ];

    public function materia()
    {
        return $this->belongsTo(Materia::class);
    }

    public function uploader()
    {
        return $this->belongsTo(User::class, 'user_id');
    }
}