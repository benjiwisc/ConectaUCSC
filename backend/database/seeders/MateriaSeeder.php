<?php

namespace Database\Seeders;

use App\Models\Carrera;
use App\Models\Materia;
use Illuminate\Database\Seeder;

class MateriaSeeder extends Seeder
{
    public function run(): void
    {
        $materias = [
            'Ingeniería Civil Informática' => [
                'Cálculo I', 'Cálculo II', 'Álgebra Lineal',
                'Programación I', 'Programación II', 'Estructura de Datos',
                'Base de Datos', 'Redes de Computadores', 'Sistemas Operativos',
                'Ingeniería de Software', 'Arquitectura de Computadores',
            ],
            'Ingeniería Civil Industrial' => [
                'Cálculo I', 'Cálculo II', 'Álgebra Lineal',
                'Estadística', 'Investigación de Operaciones',
                'Gestión de Proyectos', 'Economía', 'Contabilidad',
            ],
            'Ingeniería Civil Eléctrica' => [
                'Cálculo I', 'Cálculo II', 'Física I', 'Física II',
                'Circuitos Eléctricos', 'Electrónica', 'Máquinas Eléctricas',
                'Sistemas de Control', 'Electromagnetismo',
            ],
            'Bioquímica' => [
                'Química General', 'Química Orgánica', 'Biología Celular',
                'Bioquímica I', 'Bioquímica II', 'Microbiología',
                'Genética', 'Inmunología',
            ],
            'Química y Farmacia' => [
                'Química General', 'Química Orgánica', 'Farmacología',
                'Farmacognosia', 'Toxicología', 'Biofarmacia',
            ],
            'Medicina' => [
                'Anatomía', 'Fisiología', 'Bioquímica',
                'Patología', 'Farmacología', 'Semiología',
                'Medicina Interna', 'Cirugía',
            ],
            'Enfermería' => [
                'Anatomía', 'Fisiología', 'Fundamentos de Enfermería',
                'Salud del Adulto', 'Salud Mental', 'Pediatría',
            ],
            'Kinesiología' => [
                'Anatomía', 'Fisiología', 'Biomecánica',
                'Kinesiología Musculoesquelética', 'Neurología',
            ],
            'Nutrición y Dietética' => [
                'Bioquímica', 'Fisiología', 'Nutrición Básica',
                'Dietoterapia', 'Salud Pública',
            ],
            'Tecnología Médica' => [
                'Anatomía', 'Fisiología', 'Laboratorio Clínico',
                'Imagenología', 'Oftalmología',
            ],
            'Pedagogía en Educación Básica' => [
                'Didáctica General', 'Psicología Educacional',
                'Matemática Básica', 'Lenguaje y Comunicación',
                'Ciencias Naturales', 'Historia',
            ],
            'Pedagogía en Educación Diferencial' => [
                'Psicología del Desarrollo', 'Neuropsicología',
                'Didáctica Diferencial', 'Lenguaje y Aprendizaje',
            ],
            'Pedagogía en Inglés' => [
                'Inglés I', 'Inglés II', 'Inglés III',
                'Lingüística', 'Literatura en Inglés', 'Fonética',
            ],
            'Pedagogía en Matemática' => [
                'Cálculo I', 'Álgebra', 'Geometría',
                'Estadística', 'Didáctica de la Matemática',
            ]
        ];

        foreach ($materias as $carreraNombre => $listaMaterias) {
            $carrera = Carrera::where('nombre', $carreraNombre)->first();
            if ($carrera) {
                foreach ($listaMaterias as $materia) {
                    Materia::create([
                        'nombre' => $materia,
                        'carrera_id' => $carrera->id,
                    ]);
                }
            }
        }
    }
}