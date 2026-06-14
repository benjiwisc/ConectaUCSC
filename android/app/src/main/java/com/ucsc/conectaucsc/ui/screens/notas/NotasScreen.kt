package com.ucsc.conectaucsc.ui.screens.notas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.Grade
import com.ucsc.conectaucsc.ui.navigation.AddNotas
import com.ucsc.conectaucsc.ui.navigation.EditNotas
import com.ucsc.conectaucsc.ui.viewmodel.NotasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasScreen(
    navController: NavController,
    recordId: Int,
    notasViewModel: NotasViewModel
) {
    val context = LocalContext.current
    val notas by notasViewModel.notas.collectAsState()
    val isLoading by notasViewModel.isLoading.collectAsState()
    val mensaje by notasViewModel.mensaje.collectAsState()

    LaunchedEffect(recordId) {
        notasViewModel.cargarNotas(recordId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            notasViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas de Asignatura") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AddNotas(recordId)) }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Nota")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (notas.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay notas registradas aún", fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notas) { nota ->
                        CardNotaItem(
                            nota = nota,
                            onEditClick = { navController.navigate(EditNotas(recordId, nota.id)) },
                            onDeleteClick = { notasViewModel.deleteNota(nota.id, recordId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardNotaItem(nota: Grade, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nota.evaluacion, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Porcentaje: ${nota.porcentaje}%", fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f", nota.nota),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}