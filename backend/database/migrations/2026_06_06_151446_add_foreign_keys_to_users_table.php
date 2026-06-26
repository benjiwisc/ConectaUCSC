<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    


    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->foreign('facultad_id')->references('id')->on('facultads')->nullOnDelete();
            $table->foreign('carrera_id')->references('id')->on('carreras')->nullOnDelete();
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropForeign(['facultad_id']);
            $table->dropForeign(['carrera_id']);
        });
    }
};
