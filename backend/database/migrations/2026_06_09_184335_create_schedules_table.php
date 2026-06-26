<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('schedules', function (Blueprint $table) {
            $table->id();
            $table->foreignId('record_id')->constrained('records')->onDelete('cascade');
            $table->enum('dia', ['lunes','martes','miércoles','jueves','viernes','sábado']);
            $table->time('hora_inicio');
            $table->time('hora_fin');
            $table->enum('tipo_clase', ['laboratorio','catedra','ayudantia']);
            $table->string('sala')->nullable();
            $table->timestamps();
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('schedules');
    }
};
