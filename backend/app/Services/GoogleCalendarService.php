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

            
            $accessToken = $client->fetchAccessTokenWithRefreshToken($refreshToken);
            $client->setAccessToken($accessToken);
            
            return $client;
        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al autenticar cliente Google OAuth: ' . $e->getMessage());
            return null;
        }
    }

    


    public static function createEvent(SesionEstudio $sesion)
    {
        $client = self::getGoogleClient();
        if (!$client) {
            return null;
        }

        try {
            $service = new Calendar($client);

            
            $startTime = Carbon::parse($sesion->fecha_hora);
            $endTime = (clone $startTime)->addHours(1)->addMinutes(30);

            
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

            
            
            $createdEvent = $service->events->insert('primary', $event, ['sendUpdates' => 'all']);

            return $createdEvent->getId();
        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al crear el evento para la sesión ' . $sesion->id . ': ' . $e->getMessage());
            return null;
        }
    }

    


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

            
            $event = $service->events->get('primary', $sesion->google_event_id);

            
            $systemEmail = env('GOOGLE_SYSTEM_EMAIL');
            $attendees = [];
            $participantes = $sesion->participantes()->select('email')->get();
            foreach ($participantes as $p) {
                if (!empty($p->email) && strtolower($p->email) !== strtolower($systemEmail)) {
                    $attendees[] = ['email' => $p->email];
                }
            }

            
            $event->setAttendees($attendees);

            
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

            
            $service->events->update('primary', $sesion->google_event_id, $event, ['sendUpdates' => 'all']);

        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al sincronizar el evento ' . $sesion->google_event_id . ': ' . $e->getMessage());
        }
    }

    


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
            
            $service->events->delete('primary', $googleEventId, ['sendUpdates' => 'all']);
        } catch (\Exception $e) {
            Log::error('Google Calendar API: Error al eliminar el evento ' . $googleEventId . ': ' . $e->getMessage());
        }
    }
}
