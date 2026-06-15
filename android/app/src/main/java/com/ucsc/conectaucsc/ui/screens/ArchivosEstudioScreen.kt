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
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
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
import com.ucsc.conectaucsc.data.model.ArchivoEstudio
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import com.ucsc.conectaucsc.ui.viewmodel.ArchivosEstudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivosEstudioScreen(
    navController: NavController,
    materiaId: Int,
    viewModel: ArchivosEstudioViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val archivos by viewModel.archivos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val context = LocalContext.current
    
    val currentUserId = authViewModel.getUserId()
    val currentUserName = authViewModel.getUserName()

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarArchivos(materiaId)
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
                title = { Text("Archivos de Estudio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Subir Archivo")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (archivos.isEmpty()) {
                Text(
                    "No hay archivos disponibles para esta materia",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(archivos) { archivo ->
                        ArchivoItem(
                            archivo = archivo,
                            currentUserId = currentUserId,
                            currentUserName = currentUserName,
                            onDescargar = {
                                viewModel.descargarArchivo(
                                    context,
                                    archivo.id,
                                    archivo.file_name ?: "archivo_${archivo.id}",
                                    archivo.mime_type
                                )
                            },
                            onEliminar = { viewModel.eliminarArchivo(archivo.id, materiaId) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            SubirArchivoDialog(
                onDismiss = { showDialog = false },
                onConfirm = { titulo, descripcion, uri ->
                    viewModel.subirArchivo(context, materiaId, titulo, descripcion, uri)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun ArchivoItem(
    archivo: ArchivoEstudio,
    currentUserId: Int,
    currentUserName: String,
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
                    Text(
                        text = archivo.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = when (archivo.file_extension?.lowercase()) {
                        "pdf" -> Icons.Default.PictureAsPdf
                        "doc", "docx" -> Icons.Default.Description
                        "ppt", "pptx" -> Icons.Default.Slideshow
                        else -> Icons.AutoMirrored.Filled.InsertDriveFile
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = archivo.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Archivo: ${archivo.file_name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentUserId == archivo.user_id) {
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
                
                Button(onClick = onDescargar) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Descargar")
                }
            }
        }
    }
}

@Composable
fun SubirArchivoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Uri) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("Ningún archivo seleccionado") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            selectedUri = it
            fileName = getFileName(context, it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Subir Archivo de Estudio") },
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
                    onClick = { 
                        launcher.launch(arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-powerpoint",
                            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                        )) 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar Archivo")
                }
                Text(text = fileName, style = MaterialTheme.typography.labelSmall)
                Text(
                    "Formatos permitidos: PDF, Word, PowerPoint",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedUri?.let { onConfirm(titulo, descripcion, it) } },
                enabled = titulo.isNotBlank() && descripcion.isNotBlank() && selectedUri != null
            ) {
                Text("Subir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getFileName(context: android.content.Context, uri: Uri): String {
    var name = "archivo"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (index != -1) name = it.getString(index)
        }
    }
    return name
}