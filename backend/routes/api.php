<?php

use App\Http\Controllers\Api\AuthController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\FacultadController;
use App\Http\Controllers\Api\MateriaController;

// Rutas públicas
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login',    [AuthController::class, 'login']);
Route::get('/facultades', [FacultadController::class, 'index']);
Route::get('/facultades/{id}/carreras', [FacultadController::class, 'carreras']);
Route::get('/carreras/{id}/materias', [MateriaController::class, 'porCarrera']);

// Rutas protegidas
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me',      [AuthController::class, 'me']);

    Route::get('/mis-materias', [MateriaController::class, 'misMateria']);
    Route::post('/mis-materias', [MateriaController::class, 'agregar']);
    Route::delete('/mis-materias/{materiaId}', [MateriaController::class, 'eliminar']);
});