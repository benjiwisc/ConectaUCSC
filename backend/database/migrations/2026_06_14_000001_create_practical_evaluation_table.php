<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::create('practical_evaluation', function (Blueprint $table) {
            $table->id();
            $table->foreignId('materia_id')->constrained('materias')->onDelete('cascade');
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->string('titulo');
            $table->text('descripcion');
            $table->string('pdf_path');
            $table->timestamps();

            $table->index(['materia_id', 'user_id']);
        });

        Schema::create('practical_evaluation_completions', function (Blueprint $table) {
            $table->id();
            $table->foreignId('practical_evaluation_id')->constrained('practical_evaluation')->onDelete('cascade');
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->timestamp('completed_at');
            $table->timestamps();

            $table->unique(['practical_evaluation_id', 'user_id'], 'pe_completion_unique');
        });
    }

    


    public function down(): void
    {
        Schema::dropIfExists('practical_evaluation_completions');
        Schema::dropIfExists('practical_evaluation');
    }
};