<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\FileManagement;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class FileManagementController extends Controller
{
    public function porMateria(Request $request, int $materiaId)
    {
        $user = $request->user();

        if (!$user->materias()->where('materia_id', $materiaId)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a los archivos de esta materia',
            ], 403);
        }

        $archivos = FileManagement::with('uploader:id,name')
            ->where('materia_id', $materiaId)
            ->orderByDesc('created_at')
            ->get();

        return response()->json($archivos);
    }

    public function store(Request $request, int $materiaId)
    {
        $request->validate([
            'titulo' => 'required|string|max:255',
            'descripcion' => 'required|string',
            'archivo' => 'required|file|mimes:pdf,doc,docx,ppt,pptx|max:20480',
        ]);

        $user = $request->user();

        if (!$user->materias()->where('materia_id', $materiaId)->exists()) {
            return response()->json([
                'message' => 'No puedes subir archivos a una materia que no cursas',
            ], 403);
        }

        $uploadedFile = $request->file('archivo');
        $originalName = $uploadedFile->getClientOriginalName();
        $extension = strtolower($uploadedFile->getClientOriginalExtension());
        $mimeType = $uploadedFile->getMimeType();
        $filePath = $uploadedFile->store('file-management', 'public');

        $archivo = FileManagement::create([
            'materia_id' => $materiaId,
            'user_id' => $user->id,
            'titulo' => $request->titulo,
            'descripcion' => $request->descripcion,
            'file_path' => $filePath,
            'file_name' => $originalName,
            'file_extension' => $extension,
            'mime_type' => $mimeType,
        ]);

        return response()->json($archivo->load('uploader:id,name'), 201);
    }

    public function download(Request $request, int $fileId)
    {
        $user = $request->user();
        $archivo = FileManagement::findOrFail($fileId);

        if (!$user->materias()->where('materia_id', $archivo->materia_id)->exists()) {
            return response()->json([
                'message' => 'No tienes acceso a este archivo',
            ], 403);
        }

        if (!Storage::disk('public')->exists($archivo->file_path)) {
            return response()->json([
                'message' => 'El archivo no está disponible',
            ], 404);
        }

        return Storage::disk('public')->download($archivo->file_path, $archivo->file_name);
    }

    public function destroy(Request $request, int $fileId)
    {
        $user = $request->user();
        $archivo = FileManagement::findOrFail($fileId);

        if ($archivo->user_id !== $user->id) {
            return response()->json([
                'message' => 'Solo el alumno que subió el archivo puede eliminarlo',
            ], 403);
        }

        if ($archivo->file_path) {
            Storage::disk('public')->delete($archivo->file_path);
        }

        $archivo->delete();

        return response()->json(['message' => 'Archivo eliminado correctamente']);
    }
}