<?php

namespace Database\Seeders;

use App\Models\Carrera;
use App\Models\Facultad;
use Illuminate\Database\Seeder;

class FacultadCarreraSeeder extends Seeder
{
    public function run(): void
    {
        $data = [
            'Facultad de Ingeniería' => [
                'Ingeniería Civil Informática',
                'Ingeniería Civil Industrial',
                'Ingeniería Civil Eléctrica',
                'Ingeniería Civil Mecánica',
            ],
            'Facultad de Ciencias' => [
                'Bioquímica',
                'Química y Farmacia',
            ],
            'Facultad de Medicina' => [
                'Medicina',
                'Enfermería',
                'Kinesiología',
                'Nutrición y Dietética',
                'Tecnología Médica',
            ],
            'Facultad de Educación' => [
                'Pedagogía en Educación Básica',
                'Pedagogía en Educación Diferencial',
                'Pedagogía en Inglés',
                'Pedagogía en Matemática',
            ]
        ];

        foreach ($data as $facultadNombre => $carreras) {
            $facultad = Facultad::create(['nombre' => $facultadNombre]);
            foreach ($carreras as $carrera) {
                Carrera::create([
                    'nombre' => $carrera,
                    'facultad_id' => $facultad->id,
                ]);
            }
        }
    }
}