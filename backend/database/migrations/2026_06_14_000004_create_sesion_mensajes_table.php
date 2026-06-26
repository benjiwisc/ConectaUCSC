<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('sesion_mensajes', function (Blueprint $table) {
            $table->id();
            $table->foreignId('sesion_estudio_id')->constrained('sesion_estudios')->onDelete('cascade');
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->text('message');
            $table->timestamps();

            $table->index(['sesion_estudio_id', 'created_at']);
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('sesion_mensajes');
    }
};