package com.ucsc.conectaucsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ucsc.conectaucsc.utils.TomaGps
import com.ucsc.conectaucsc.ui.navigation.Navigation
import com.ucsc.conectaucsc.ui.theme.ConectaUCSCTheme
import com.ucsc.conectaucsc.ui.viewmodel.AsistenciasViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val asistenciaViewModel: AsistenciasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TomaGps(
            activity = this,
            onUbicacionObtenida = { lat, lng ->
                asistenciaViewModel.registrarAsistencia(lat, lng)
            }
        ).iniciar()

        setContent {
            ConectaUCSCTheme {
                Navigation()
            }
        }
    }
}