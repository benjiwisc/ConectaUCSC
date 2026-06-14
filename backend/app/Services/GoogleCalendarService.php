<?php

namespace App\Services;

use App\Models\SesionEstudio;
use Carbon\Carbon;
use Google\Client;
use Google\Service\Calendar;
use Google\Service\Calendar\Event;
use Google\Service\Calendar\EventDateTime;
use Illuminate\Support\Facades\Log;

class GoogleCalendarService
{
    /**
     * Obtiene el cliente de Google configurado con OAuth 2.0 y el Refresh Token.
     */
    protected static function getGoogleClient()
    {
        $clientId = env('GOOGLE_CLIENT_ID');
        $clientSecret = env('GOOGLE_CLIENT_SECRET');
        $refreshToken = env('GOOGLE_REFRESH_TOKEN');

        if (empty($clientId) || empty($clientSecret) || empty($refreshToken)) {
            Log::error('Google Calendar API: Las credenciales de OAuth no están configuradas en el archivo .env');
            return null;
        }

        try {
            $client = new Client();
            $client->setClientId($clientId);
            $client->setClientSecret($clientSecret);
            $client->addScope(Calendar::CALENDAR);
            $client->setAccessType('offline');

            // Autenticar usando el refresh token y establecer el token de acceso
            $accessToken = $client->fetchAccessTokenWithRefreshToken($refreshToken);
            $client->setAccessToken($accessToken);
            
            return $client;
        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al autenticar cliente Google OAuth: ' . $e->getMessage());
            return null;
        }
    }

    /**
     * Crea un evento en Google Calendar para una sesión de estudio.
     */
    public static function createEvent(SesionEstudio $sesion)
    {
        $client = self::getGoogleClient();
        if (!$client) {
            return null;
        }

        try {
            $service = new Calendar($client);

            // Tiempos de inicio y fin (duración por defecto de 1.5 horas)
            $startTime = Carbon::parse($sesion->fecha_hora);
            $endTime = (clone $startTime)->addHours(1)->addMinutes(30);

            // Obtener correos de los participantes (filtrando el del sistema para evitar auto-invitación)
            $systemEmail = env('GOOGLE_SYSTEM_EMAIL');
            $attendees = [];
            $participantes = $sesion->participantes()->select('email')->get();
            foreach ($participantes as $p) {
                if (!empty($p->email) && strtolower($p->email) !== strtolower($systemEmail)) {
                    $attendees[] = ['email' => $p->email];
                }
            }

            $tutorName = $sesion->creador ? $sesion->creador->name : 'Sin tutor';

            $event = new Event([
                'summary' => 'Tutoría: ' . $sesion->titulo . ' (Tutor: ' . $tutorName . ')',
                'location' => $sesion->lugar,
                'description' => $sesion->descripcion ?? 'Sesión de estudio organizada a través de ConectaUCSC.',
                'start' => new EventDateTime([
                    'dateTime' => $startTime->toIso8601String(),
                    'timeZone' => config('app.timezone', 'America/Santiago'),
                ]),
                'end' => new EventDateTime([
                    'dateTime' => $endTime->toIso8601String(),
                    'timeZone' => config('app.timezone', 'America/Santiago'),
                ]),
                'attendees' => $attendees,
                'reminders' => [
                    'useDefault' => false,
                    'overrides' => [
                        ['method' => 'email', 'minutes' => 24 * 60],
                        ['method' => 'popup', 'minutes' => 30],
                    ],
                ],
            ]);

            // Insertar el evento en el calendario principal ('primary' mapea al del usuario autenticado)
            // sendUpdates => 'all' envía las invitaciones por correo a los asistentes
            $createdEvent = $service->events->insert('primary', $event, ['sendUpdates' => 'all']);

            return $createdEvent->getId();
        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al crear el evento para la sesión ' . $sesion->id . ': ' . $e->getMessage());
            return null;
        }
    }

    /**
     * Sincroniza los participantes de una sesión en el evento de Google Calendar.
     */
    public static function syncEvent(SesionEstudio $sesion)
    {
        if (empty($sesion->google_event_id)) {
            $eventId = self::createEvent($sesion);
            if ($eventId) {
                $sesion->update(['google_event_id' => $eventId]);
            }
            return;
        }

        $client = self::getGoogleClient();
        if (!$client) {
            return;
        }

        try {
            $service = new Calendar($client);

            // Obtener el evento actual de Google Calendar
            $event = $service->events->get('primary', $sesion->google_event_id);

            // Obtener correos actuales de los participantes (filtrando el del sistema)
            $systemEmail = env('GOOGLE_SYSTEM_EMAIL');
            $attendees = [];
            $participantes = $sesion->participantes()->select('email')->get();
            foreach ($participantes as $p) {
                if (!empty($p->email) && strtolower($p->email) !== strtolower($systemEmail)) {
                    $attendees[] = ['email' => $p->email];
                }
            }

            // Actualizar invitados
            $event->setAttendees($attendees);

            // Actualizar otros metadatos si han cambiado
            $tutorName = $sesion->creador ? $sesion->creador->name : 'Sin tutor';
            $event->setSummary('Tutoría: ' . $sesion->titulo . ' (Tutor: ' . $tutorName . ')');
            $event->setLocation($sesion->lugar);
            $event->setDescription($sesion->descripcion ?? 'Sesión de estudio organizada a través de ConectaUCSC.');

            $startTime = Carbon::parse($sesion->fecha_hora);
            $endTime = (clone $startTime)->addHours(1)->addMinutes(30);

            $event->setStart(new EventDateTime([
                'dateTime' => $startTime->toIso8601String(),
                'timeZone' => config('app.timezone', 'America/Santiago'),
            ]));
            $event->setEnd(new EventDateTime([
                'dateTime' => $endTime->toIso8601String(),
                'timeZone' => config('app.timezone', 'America/Santiago'),
            ]));

            // Guardar cambios y notificar a los nuevos/removidos
            $service->events->update('primary', $sesion->google_event_id, $event, ['sendUpdates' => 'all']);

        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al sincronizar el evento ' . $sesion->google_event_id . ': ' . $e->getMessage());
        }
    }

    /**
     * Elimina el evento de Google Calendar.
     */
    public static function deleteEvent($googleEventId)
    {
        if (empty($googleEventId)) {
            return;
        }

        $client = self::getGoogleClient();
        if (!$client) {
            return;
        }

        try {
            $service = new Calendar($client);
            // sendUpdates => 'all' notifica a los invitados que el evento fue cancelado
            $service->events->delete('primary', $googleEventId, ['sendUpdates' => 'all']);
        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al eliminar el evento ' . $googleEventId . ': ' . $e->getMessage());
        }
    }
}
