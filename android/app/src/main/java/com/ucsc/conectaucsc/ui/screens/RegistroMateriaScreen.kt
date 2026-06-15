package com.ucsc.conectaucsc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucsc.conectaucsc.ui.navigation.ArchivosEstudio
import com.ucsc.conectaucsc.ui.navigation.Asistencia
import com.ucsc.conectaucsc.ui.navigation.EvaluacionesPracticas
import com.ucsc.conectaucsc.ui.navigation.Horario
import com.ucsc.conectaucsc.ui.navigation.Logros
import com.ucsc.conectaucsc.ui.navigation.Notas
import com.ucsc.conectaucsc.ui.navigation.Sesiones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroMateriaScreen(
    navController: NavController,
    registroId: Int,
    materiaId: Int,
    nombreMateria: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Materia") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(
                12.dp,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val botones = listOf(
                "Asistencia" to Icons.Default.DateRange,
                "Logros" to Icons.Default.Star,
                "Notas" to Icons.Default.Edit,
                "Horario" to Icons.Default.Schedule,
                "Sesiones" to Icons.Default.Schedule,
                "Evaluaciones Prácticas" to Icons.AutoMirrored.Filled.Assignment,
                "Archivos de Estudio" to Icons.Default.Folder
            )

            botones.forEach { (titulo, icono) ->
                Button(
                    onClick = {
                        when (titulo) {
                            "Asistencia" -> navController.navigate(Asistencia(registroId))
                            "Horario" -> navController.navigate(Horario(registroId))
                            "Notas" -> navController.navigate(Notas(registroId))
                            "Logros" -> navController.navigate(Logros(registroId))
                            "Sesiones" -> navController.navigate(Sesiones(materiaId, nombreMateria))
                            "Evaluaciones Prácticas" -> navController.navigate(EvaluacionesPracticas(materiaId))
                            "Archivos de Estudio" -> navController.navigate(ArchivosEstudio(materiaId))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = titulo
                    )
                    Text(
                        text = titulo,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}