<?php

use App\Http\Controllers\Api\AuthController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\FacultadController;
use App\Http\Controllers\Api\MateriaController;
use App\Http\Controllers\ScheduleController;
use App\Http\Controllers\RecordController;
use App\Http\Controllers\GradeController;
use App\Http\Controllers\AttendanceController;
use App\Http\Controllers\AttainmentController;

// Rutas públicas
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login',    [AuthController::class, 'login']);
Route::get('/facultades', [FacultadController::class, 'index']);
Route::get('/facultades/{id}/carreras', [FacultadController::class, 'carreras']);
Route::get('/carreras/{id}/materias', [MateriaController::class, 'porCarrera']);

// Rutas protegidas
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout',[AuthController::class, 'logout']);
    Route::get('/me',[AuthController::class, 'me']);

    Route::get('/mis-materias',[MateriaController::class, 'misMateria']);
    Route::post('/mis-materias',[MateriaController::class, 'agregar']);
    Route::delete('/mis-materias/{materiaId}',[MateriaController::class, 'eliminar']);

    Route::prefix('horarios')->group(function () {
        Route::get('/{recordId}',[ScheduleController::class, 'index']);
        Route::get('editar/{recordId}',[ScheduleController::class, 'edit']);
        Route::post('/',[ScheduleController::class, 'store']);
        Route::put('/{id}',[ScheduleController::class, 'update']);
        Route::delete('/{id}',[ScheduleController::class, 'destroy']);
    });

    Route::prefix('registro')->group(function () {
        Route::get('/{usuarioMateriaId}',[RecordController::class, 'show']);
        Route::post('/{usuarioMateriaId}',[RecordController::class, 'store']);
        Route::delete('/{usuarioMateriaId}',[RecordController::class, 'destroy']);
        Route::put('/{id}/finalizar',[RecordController::class, 'finalizar']);
    });

    Route::prefix('notas')->group(function () {
        Route::get('/{recordId}',[GradeController::class, 'index']);
        Route::get('editar/{recordId}',[GradeController::class, 'edit']);
        Route::post('/',[GradeController::class, 'store']);
        Route::put('/{id}',[GradeController::class, 'update']);
        Route::delete('/{id}',[GradeController::class, 'destroy']);
        Route::get('/{recordId}/promedio',[GradeController::class, 'promedio']);
    });

    Route::prefix('asistencias')->group(function () {
        Route::post('/registrar',[AttendanceController::class, 'registrarPorUbicacion']);
        Route::get('/{recordId}',[AttendanceController::class, 'index']);
        Route::post('/',[AttendanceController::class, 'store']);
        Route::put('/{id}',[AttendanceController::class, 'update']);
        Route::put('/{id}/justificar',[AttendanceController::class, 'justificar']);
        Route::delete('/{id}',[AttendanceController::class, 'destroy']);
    });

    Route::prefix('logros')->group(function () {
        Route::get('/{recordId}',[AttainmentController::class, 'index']);
    });


});