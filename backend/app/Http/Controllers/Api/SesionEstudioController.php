<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\SesionMensaje;
use App\Models\SesionEstudio;
use Illuminate\Http\Request;

class SesionEstudioController extends Controller
{
    private function puedeAccederSesion($user, SesionEstudio $sesion): bool
    {
        return $sesion->participantes()->where('user_id', $user->id)->exists();
    }

    // Listar sesiones por materia
    public function porMateria(Request $request, $materiaId)
    {
        $user = $request->user();

        // Verificar que el usuario cursa esa materia
        if (!$user->materias()->where('materia_id', $materiaId)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a las sesiones de esta materia'
            ], 403);
        }

        $sesiones = SesionEstudio::with(['creador:id,name', 'participantes:id,name'])
            ->where('materia_id', $materiaId)
            ->orderBy('fecha_hora')
            ->get();

        return response()->json($sesiones);
    }

    // Crear sesión
    public function crear(Request $request)
    {
        $request->validate([
            'titulo'      => 'required|string|max:255',
            'lugar'       => 'required|string|max:255',
            'fecha_hora'  => 'required|date',
            'descripcion' => 'nullable|string',
            'materia_id'  => 'required|exists:materias,id',
        ]);

        // Verificar que el usuario cursa esa materia
        $user = $request->user();
        if (!$user->materias()->where('materia_id', $request->materia_id)->exists()) {
            return response()->json([
                'message' => 'No puedes crear una sesión para una materia que no cursas'
            ], 403);
        }

        $sesion = SesionEstudio::create([
            'titulo'      => $request->titulo,
            'lugar'       => $request->lugar,
            'fecha_hora'  => $request->fecha_hora,
            'descripcion' => $request->descripcion,
            'materia_id'  => $request->materia_id,
            'user_id'     => $user->id,
        ]);

        // El creador se une automáticamente
        $sesion->participantes()->attach($user->id);

        // Crear evento en Google Calendar
        $sesion->load(['creador:id,name,email', 'participantes:id,name,email']);
        $eventId = \App\Services\GoogleCalendarService::createEvent($sesion);
        if ($eventId) {
            $sesion->update(['google_event_id' => $eventId]);
        }

        return response()->json($sesion->load(['creador:id,name', 'participantes:id,name']), 201);
    }

    // Unirse a una sesión
    public function unirse(Request $request, $sesionId)
    {
        $user = $request->user();
        $sesion = SesionEstudio::findOrFail($sesionId);

        if ($sesion->participantes()->where('user_id', $user->id)->exists()) {
            return response()->json(['message' => 'Ya eres participante de esta sesión'], 409);
        }

        $sesion->participantes()->attach($user->id);

        // Sincronizar participantes en Google Calendar
        $sesion->load(['participantes:id,name,email']);
        \App\Services\GoogleCalendarService::syncEvent($sesion);

        return response()->json(['message' => 'Te has unido a la sesión correctamente']);
    }

    // Mis sesiones creadas
    public function misSesiones(Request $request)
    {
        $sesiones = SesionEstudio::with(['materia:id,nombre', 'participantes:id,name'])
            ->where('user_id', $request->user()->id)
            ->orderBy('fecha_hora')
            ->get();

        return response()->json($sesiones);
    }
    
    // Salirse de una sesión
    public function salirse(Request $request, $sesionId)
    {
        $user = $request->user();
        $sesion = SesionEstudio::findOrFail($sesionId);

        if ($sesion->user_id === $user->id) {
            return response()->json([
                'message' => 'El creador no puede abandonar la sesión, debe finalizarla'
            ], 403);
        }

        $sesion->participantes()->detach($user->id);

        // Sincronizar participantes en Google Calendar
        $sesion->load(['participantes:id,name,email']);
        \App\Services\GoogleCalendarService::syncEvent($sesion);

        return response()->json(['message' => 'Has abandonado la sesión']);
    }

    // Finalizar sesión (solo el creador)
    public function finalizar(Request $request, $sesionId)
    {
        $user = $request->user();
        $sesion = SesionEstudio::findOrFail($sesionId);

        if ($sesion->user_id !== $user->id) {
            return response()->json([
                'message' => 'Solo el creador puede finalizar la sesión'
            ], 403);
        }

        $googleEventId = $sesion->google_event_id;
        $sesion->mensajes()->delete();
        $sesion->delete();

        // Eliminar evento en Google Calendar si existe
        if ($googleEventId) {
            \App\Services\GoogleCalendarService::deleteEvent($googleEventId);
        }

        return response()->json(['message' => 'Sesión finalizada y eliminada correctamente']);
    }

    // Listar y filtrar todas las sesiones de las materias del usuario
    public function filtrar(Request $request)
    {
        $user = $request->user();
        $materiaIds = $user->materias()->pluck('materias.id')->toArray();

        $query = SesionEstudio::with(['creador:id,name', 'participantes:id,name', 'materia:id,nombre'])
            ->whereIn('materia_id', $materiaIds);

        // Buscar por título, ayudante (creador) o nombre del ramo (materia)
        if ($request->has('buscar') && !empty($request->buscar)) {
            $buscar = $request->buscar;
            $query->where(function($q) use ($buscar) {
                $q->where('titulo', 'like', "%{$buscar}%")
                  ->orWhereHas('creador', function($q) use ($buscar) {
                      $q->where('name', 'like', "%{$buscar}%");
                  })
                  ->orWhereHas('materia', function($q) use ($buscar) {
                      $q->where('nombre', 'like', "%{$buscar}%");
                  });
            });
        }

        // Ordenar por fecha_hora
        $orden = $request->query('orden', 'recientes');
        if ($orden === 'antiguos') {
            $query->orderBy('fecha_hora', 'asc');
        } else {
            $query->orderBy('fecha_hora', 'desc');
        }

        $sesiones = $query->get();
        return response()->json($sesiones);
    }

    // Listar mensajes de una sesión
    public function mensajes(Request $request, $sesionId)
    {
        $user = $request->user();
        $sesion = SesionEstudio::findOrFail($sesionId);

        if (!$this->puedeAccederSesion($user, $sesion)) {
            return response()->json([
                'message' => 'No tienes acceso al chat de esta sesión'
            ], 403);
        }

        $mensajes = SesionMensaje::with('user:id,name')
            ->where('sesion_estudio_id', $sesion->id)
            ->orderBy('created_at')
            ->get();

        return response()->json($mensajes);
    }

    // Enviar mensaje al chat de la sesión
    public function enviarMensaje(Request $request, $sesionId)
    {
        $request->validate([
            'message' => 'required|string|max:2000',
        ]);

        $user = $request->user();
        $sesion = SesionEstudio::findOrFail($sesionId);

        if (!$this->puedeAccederSesion($user, $sesion)) {
            return response()->json([
                'message' => 'No tienes acceso al chat de esta sesión'
            ], 403);
        }

        $mensaje = SesionMensaje::create([
            'sesion_estudio_id' => $sesion->id,
            'user_id' => $user->id,
            'message' => $request->message,
        ]);

        return response()->json($mensaje->load('user:id,name'), 201);
    }
}