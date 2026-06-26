<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('sesion_participantes', function (Blueprint $table) {
            $table->id();
            $table->foreignId('sesion_estudio_id')->constrained('sesion_estudios')->onDelete('cascade');
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->unique(['sesion_estudio_id', 'user_id']);
            $table->timestamps();
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('sesion_participantes');
    }
};
