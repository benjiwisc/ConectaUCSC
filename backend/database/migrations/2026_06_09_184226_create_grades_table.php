<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('grades', function (Blueprint $table) {
            $table->id();
            $table->foreignId('record_id')->constrained('records')->onDelete('cascade');
            $table->string('evaluacion');
            $table->integer('porcentaje');
            $table->decimal('nota', 3, 1);
            $table->timestamps();
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('grades');
    }
};
