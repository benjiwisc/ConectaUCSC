<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('practical_evaluation', function (Blueprint $table) {
            if (!Schema::hasColumn('practical_evaluation', 'tipo')) {
                $table->string('tipo')->default('pdf')->after('pdf_path');
            }

            if (!Schema::hasColumn('practical_evaluation', 'contenido')) {
                $table->json('contenido')->nullable()->after('tipo');
            }
        });

        Schema::table('practical_evaluation_completions', function (Blueprint $table) {
            if (!Schema::hasColumn('practical_evaluation_completions', 'nota')) {
                $table->decimal('nota', 3, 1)->nullable()->after('completed_at');
            }

            if (!Schema::hasColumn('practical_evaluation_completions', 'puntaje')) {
                $table->decimal('puntaje', 5, 2)->nullable()->after('nota');
            }

            if (!Schema::hasColumn('practical_evaluation_completions', 'correctas')) {
                $table->unsignedInteger('correctas')->nullable()->after('puntaje');
            }

            if (!Schema::hasColumn('practical_evaluation_completions', 'total_preguntas')) {
                $table->unsignedInteger('total_preguntas')->nullable()->after('correctas');
            }

            if (!Schema::hasColumn('practical_evaluation_completions', 'respuestas')) {
                $table->json('respuestas')->nullable()->after('total_preguntas');
            }
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('practical_evaluation_completions', function (Blueprint $table) {
            if (Schema::hasColumn('practical_evaluation_completions', 'respuestas')) {
                $table->dropColumn('respuestas');
            }

            if (Schema::hasColumn('practical_evaluation_completions', 'total_preguntas')) {
                $table->dropColumn('total_preguntas');
            }

            if (Schema::hasColumn('practical_evaluation_completions', 'correctas')) {
                $table->dropColumn('correctas');
            }

            if (Schema::hasColumn('practical_evaluation_completions', 'puntaje')) {
                $table->dropColumn('puntaje');
            }

            if (Schema::hasColumn('practical_evaluation_completions', 'nota')) {
                $table->dropColumn('nota');
            }
        });

        Schema::table('practical_evaluation', function (Blueprint $table) {
            if (Schema::hasColumn('practical_evaluation', 'contenido')) {
                $table->dropColumn('contenido');
            }

            if (Schema::hasColumn('practical_evaluation', 'tipo')) {
                $table->dropColumn('tipo');
            }
        });
    }
};