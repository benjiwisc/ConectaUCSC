<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $request->validate([
            'name'        => 'required|string|max:255',
            'email'       => 'required|email|unique:users',
            'password'    => 'required|min:6',
            'facultad_id' => 'nullable|exists:facultads,id',
            'carrera_id'  => 'nullable|exists:carreras,id',
        ]);

        $user = User::create([
            'name'        => $request->name,
            'email'       => $request->email,
            'password'    => Hash::make($request->password),
            'facultad_id' => $request->facultad_id,
            'carrera_id'  => $request->carrera_id,
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user'  => $user,
        ], 201);
    }

    public function login(Request $request)
    {
        $request->validate([
            'email'    => 'required|email',
            'password' => 'required',
        ]);

        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return response()->json([
                'message' => 'Credenciales incorrectas'
            ], 401);
        }

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user'  => $user,
        ]);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Sesión cerrada correctamente'
        ]);
    }

    public function me(Request $request)
    {
        return response()->json($request->user()->load(['carrera', 'facultad']));
    }

    public function stats(Request $request)
    {
        $user = $request->user();

        $materiasCount = $user->materias()->count();

        $recordIds = \App\Models\Record::whereIn('usuario_materia_id', function ($query) use ($user) {
            $query->select('id')
                  ->from('usuario_materias')
                  ->where('user_id', $user->id);
        })->pluck('id');

        $promedioGeneral = \App\Models\Grade::whereIn('record_id', $recordIds)->avg('nota');

        $sesionesCount = \App\Models\SesionEstudio::where('user_id', $user->id)
            ->orWhereHas('participantes', function ($query) use ($user) {
                $query->where('user_id', $user->id);
            })->count();

        return response()->json([
            'materias_count' => $materiasCount,
            'promedio_general' => $promedioGeneral ? round($promedioGeneral, 2) : 0.0,
            'sesiones_count' => $sesionesCount,
        ]);
    }
}