<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Record;

class RecordController extends Controller
{
    public function show($usuarioMateriaId) {
        $record = Record::where('usuario_materia_id', $usuarioMateriaId)
            ->with(['attendances', 'grades', 'attainments', 'schedules'])
            ->firstOrFail();
        return response()->json($record);
    }

    public function store($usuarioMateriaId) {
        $record = Record::create(['usuario_materia_id' => $usuarioMateriaId]);
        return response()->json($record, 201);
    }

    public function destroy($usuarioMateriaId) {
        Record::where('usuario_materia_id', $usuarioMateriaId)->delete();
        return response()->json(null, 204);
    }

    public function finalizar($id)
    {
        $record = Record::findOrFail($id);
        $record->update(['finalizado' => true, 'fecha_fin' => now()]);

        (new AttainmentController)->verificarLogros($record);

        return response()->json($record);
    }
}
