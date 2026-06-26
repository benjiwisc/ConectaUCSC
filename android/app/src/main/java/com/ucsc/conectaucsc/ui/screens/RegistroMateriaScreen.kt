package com.ucsc.conectaucsc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                title = { Text(nombreMateria, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Panel de Asignatura",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Revisa tu asistencia, notas, material y tutorías para este ramo.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubjectOptionCard(
                    title = "Asistencia",
                    description = "Registro de firmas",
                    icon = Icons.Default.DateRange,
                    onClick = { navController.navigate(Asistencia(registroId)) },
                    modifier = Modifier.weight(1f)
                )
                SubjectOptionCard(
                    title = "Horario",
                    description = "Bloques de clases",
                    icon = Icons.Default.Schedule,
                    onClick = { navController.navigate(Horario(registroId)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubjectOptionCard(
                    title = "Notas",
                    description = "Notas y promedios",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate(Notas(registroId)) },
                    modifier = Modifier.weight(1f)
                )
                SubjectOptionCard(
                    title = "Evaluaciones",
                    description = "Tareas y certámenes",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = { navController.navigate(EvaluacionesPracticas(materiaId)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubjectOptionCard(
                    title = "Tutorías",
                    description = "Sesiones de estudio",
                    icon = Icons.Default.Groups,
                    onClick = { navController.navigate(Sesiones(materiaId, nombreMateria)) },
                    modifier = Modifier.weight(1f)
                )
                SubjectOptionCard(
                    title = "Materiales",
                    description = "Archivos de estudio",
                    icon = Icons.Default.Folder,
                    onClick = { navController.navigate(ArchivosEstudio(materiaId)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            
            SubjectOptionCard(
                title = "Logros y Metas",
                description = "Monitorea tus insignias y metas obtenidas para este ramo",
                icon = Icons.Default.Star,
                onClick = { navController.navigate(Logros(registroId)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier
            .height(115.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}