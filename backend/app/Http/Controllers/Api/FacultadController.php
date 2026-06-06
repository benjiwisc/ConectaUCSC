<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Carrera;
use App\Models\Facultad;

class FacultadController extends Controller
{
    public function index()
    {
        return response()->json(Facultad::all());
    }

    public function carreras($id)
    {
        $carreras = Carrera::where('facultad_id', $id)->get();
        return response()->json($carreras);
    }
}