package com.ucsc.conectaucsc.utils

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TomaGps(
    private val activity: ComponentActivity,
    private val onUbicacionObtenida: (latitud: Double, longitud: Double) -> Unit
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    private val permissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) obtenerUbicacion()
        }

    fun iniciar() {
        if (tienePermiso()) {
            obtenerUbicacion()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun tienePermiso(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun obtenerUbicacion() {
        activity.lifecycleScope.launch {
            val location = obtenerLocation()
            if (location != null) {
                onUbicacionObtenida(location.latitude, location.longitude)
            } else {
                android.util.Log.e("TomaGps", "No se pudo obtener ubicación")
            }
        }
    }

    private suspend fun obtenerLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val cancellationToken = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                )
                    .addOnSuccessListener { location ->
                        android.util.Log.d("TomaGps", "Ubicación obtenida: $location")
                        continuation.resume(location)
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("TomaGps", "Error al obtener ubicación: ${e.message}")
                        continuation.resume(null)
                    }

                continuation.invokeOnCancellation {
                    cancellationToken.cancel()
                }
            } catch (e: SecurityException) {
                android.util.Log.e("TomaGps", "Sin permiso: ${e.message}")
                continuation.resume(null)
            }
        }
    }
}