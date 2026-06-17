package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.PracticalEvaluationDto
import com.ucsc.conectaucsc.ui.navigation.CrearCuestionario
import com.ucsc.conectaucsc.ui.navigation.DetalleCuestionario
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import com.ucsc.conectaucsc.ui.viewmodel.EvaluacionesPracticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionesPracticasScreen(
    navController: NavController,
    materiaId: Int,
    viewModel: EvaluacionesPracticasViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val evaluaciones by viewModel.evaluaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val context = LocalContext.current
    val currentUserId = authViewModel.getUserId()

    LaunchedEffect(Unit) {
        viewModel.cargarEvaluaciones(materiaId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluaciones Prácticas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                // Navega directamente a la creación de cuestionario
                navController.navigate(CrearCuestionario(materiaId))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Evaluación")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && evaluaciones.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (evaluaciones.isEmpty()) {
                Text(
                    "No hay evaluaciones para esta materia",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(evaluaciones) { evaluation ->
                        EvaluacionCard(
                            evaluation = evaluation,
                            currentUserId = currentUserId,
                            onEliminar = { viewModel.eliminarEvaluacion(evaluation.id, materiaId) },
                            onAction = {
                                if (evaluation.tipo == "pdf") {
                                    viewModel.descargarPdf(context, evaluation.id, "${evaluation.titulo}.pdf")
                                } else {
                                    navController.navigate(DetalleCuestionario(evaluation.id))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluacionCard(
    evaluation: PracticalEvaluationDto,
    currentUserId: Int,
    onEliminar: () -> Unit,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = evaluation.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (evaluation.tipo == "pdf") "Archivo PDF" else "Cuestionario Online",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = if (evaluation.tipo == "pdf") Icons.Default.PictureAsPdf else Icons.Default.Quiz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = evaluation.descripcion, style = MaterialTheme.typography.bodyMedium)
            
            if (evaluation.hecha && evaluation.completion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Nota: ${evaluation.completion.nota} | Correctas: ${evaluation.completion.correctas}/${evaluation.completion.total_preguntas}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentUserId == evaluation.user_id) {
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
                
                Button(onClick = onAction) {
                    val label = when {
                        evaluation.tipo == "pdf" -> "Descargar PDF"
                        evaluation.hecha -> "Ver Resultados"
                        else -> "Responder"
                    }
                    Text(label)
                }
            }
        }
    }
}
