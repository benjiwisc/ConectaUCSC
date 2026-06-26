# ConectaUCSC

##  Backend (Laravel 12)

La API requiere un entorno PHP >= 8.2 y una base de datos MySQL

### Configuración e Inicio:

1. **Base de Datos**: Crear una base de datos en MySQL llamada `conectaucsc`.
2. **Entorno**: Configurar las credenciales en el archivo `backend/.env`.
   ```env
   DB_CONNECTION=mysql
   DB_HOST=127.0.0.1
   DB_PORT=3306
   DB_DATABASE=conectaucsc
   DB_USERNAME=root
   DB_PASSWORD=
   ```
3. **Instalación y Despliegue**:
   ```bash
   cd backend
   composer install
   php artisan key:generate
   php artisan migrate --seed
   php artisan serve --host=0.0.0.0 --port=8000
   ```
   *(El comando `--seed` poblará los datos iniciales de facultades, carreras y materias a través de `DatabaseSeeder`)*.

---

## Frontend (Android Client)

### Configuración de la API:

1. Importar la carpeta `android/` en Android Studio.
2. Definir la URL base de la API en el módulo de red:
   `android/app/src/main/java/com/ucsc/conectaucsc/di/NetworkModule.kt`
   ```kotlin
   // Línea 51 - Modificar según el entorno de ejecución:
   .baseUrl("http://10.0.2.2:8000/api/") // Para el Emulador de Android Studio
   // .baseUrl("http://<IP_LOCAL_HOST>:8000/api/") // Para dispositivo físico en misma red local
   ```
3. Sincronizar Gradle y compilar/ejecutar el proyecto en el dispositivo objetivo.

---

## Estructura del Repositorio

- `backend/`: API REST que expone los endpoints consumidos por la app (incluyendo integraciones como Google Calendar API en `app/Services/GoogleCalendarService.php`).
- `android/`: Código nativo Android (Vistas Compose, ViewModels, Repositorios y Servicios de red Retrofit).