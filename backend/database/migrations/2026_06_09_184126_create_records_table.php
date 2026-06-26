<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('records', function (Blueprint $table) {
            $table->id();
            $table->foreignId('usuario_materia_id')->constrained('usuario_materias')->onDelete('cascade');
            $table->boolean('finalizado')->default(false);
            $table->date('fecha_fin')->nullable();
            $table->timestamps();
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('records');
    }
};
