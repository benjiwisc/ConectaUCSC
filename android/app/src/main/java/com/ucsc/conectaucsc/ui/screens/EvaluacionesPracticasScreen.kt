package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionCard(
    evaluation: PracticalEvaluationDto,
    currentUserId: Int,
    onEliminar: () -> Unit,
    onAction: () -> Unit
) {
    val isCompleted = evaluation.hecha || evaluation.tipo == "pdf"
    val statusColor = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val statusIcon = if (evaluation.tipo == "pdf") Icons.Default.PictureAsPdf else Icons.Default.Quiz
    val typeLabel = if (evaluation.tipo == "pdf") "Archivo PDF" else "Cuestionario Online"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left border colored bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top section: Icon, Title, Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Icon container
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = statusColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Title details
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = evaluation.titulo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = typeLabel,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Suggestion Chip status
                    val statusText = when {
                        evaluation.tipo == "pdf" -> "PDF disponible"
                        evaluation.hecha -> "Resuelto"
                        else -> "Pendiente"
                    }
                    SuggestionChip(
                        onClick = {},
                        label = { Text(text = statusText, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = statusColor.copy(alpha = 0.12f),
                            labelColor = statusColor
                        ),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = evaluation.descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Nota / result representation if made
                if (evaluation.hecha && evaluation.completion != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "Nota: ${evaluation.completion.nota} | Correctas: ${evaluation.completion.correctas}/${evaluation.completion.total_preguntas}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions row at base
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUserId == evaluation.user_id) {
                        OutlinedButton(
                            onClick = onEliminar,
                            modifier = Modifier.height(36.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.8f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    val actionText = when {
                        evaluation.tipo == "pdf" -> "Descargar PDF"
                        evaluation.hecha -> "Ver Resultados"
                        else -> "Responder"
                    }
                    val actionIcon = when {
                        evaluation.tipo == "pdf" -> Icons.Default.Download
                        evaluation.hecha -> Icons.Default.Assessment
                        else -> Icons.Default.PlayArrow
                    }

                    Button(
                        onClick = onAction,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                    ) {
                        Icon(actionIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(actionText, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
