package com.ucsc.conectaucsc.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.EvaluacionPractica
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

    var showDialog by remember { mutableStateOf(false) }

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
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Evaluación")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (evaluaciones.isEmpty()) {
                Text(
                    "No hay evaluaciones prácticas registradas",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(evaluaciones) { evaluacion ->
                        EvaluacionItem(
                            evaluacion = evaluacion,
                            currentUserId = currentUserId,
                            onHecha = { viewModel.marcarComoHecha(evaluacion.id, materiaId) },
                            onDescargar = {
                                viewModel.descargarPdf(
                                    context,
                                    evaluacion.id,
                                    "Evaluacion_${evaluacion.id}.pdf"
                                )
                            },
                            onEliminar = { viewModel.eliminarEvaluacion(evaluacion.id, materiaId) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            NuevaEvaluacionDialog(
                onDismiss = { showDialog = false },
                onConfirm = { titulo, descripcion, uri ->
                    viewModel.crearEvaluacion(context, materiaId, titulo, descripcion, uri)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun EvaluacionItem(
    evaluacion: EvaluacionPractica,
    currentUserId: Int,
    onHecha: () -> Unit,
    onDescargar: () -> Unit,
    onEliminar: () -> Unit
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = evaluacion.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (evaluacion.hecha) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Hecha",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = evaluacion.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            
            // Mostrar el nombre del archivo si existe en la ruta
            val fileName = evaluacion.pdf_path?.split("/")?.last() ?: "archivo_evaluacion.pdf"
            Text(
                text = "Archivo: $fileName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentUserId == evaluacion.user_id) {
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
                
                IconButton(onClick = onDescargar) {
                    Icon(Icons.Default.Download, contentDescription = "Descargar PDF")
                }
                
                if (!evaluacion.hecha) {
                    Button(
                        onClick = onHecha,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Marcar Hecha")
                    }
                } else {
                    Text(
                        "Completada",
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun NuevaEvaluacionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Uri?) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("Ningún archivo seleccionado") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
        fileName = uri?.path?.split("/")?.last() ?: "Archivo seleccionado"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Evaluación Práctica") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { launcher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar PDF")
                }
                Text(text = fileName, style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(titulo, descripcion, selectedUri) },
                enabled = titulo.isNotBlank() && descripcion.isNotBlank() && selectedUri != null
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}