<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('file_management', function (Blueprint $table) {
            $table->id();
            $table->foreignId('materia_id')->constrained('materias')->onDelete('cascade');
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->string('titulo');
            $table->text('descripcion');
            $table->string('file_path');
            $table->string('file_name');
            $table->string('file_extension', 10);
            $table->string('mime_type')->nullable();
            $table->timestamps();

            $table->index(['materia_id', 'user_id']);
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('file_management');
    }
};