<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        DB::statement("ALTER TABLE file_management ADD CONSTRAINT file_management_file_extension_check CHECK (file_extension IN ('pdf', 'doc', 'docx', 'ppt', 'pptx'))");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        DB::statement('ALTER TABLE file_management DROP CHECK file_management_file_extension_check');
    }
};