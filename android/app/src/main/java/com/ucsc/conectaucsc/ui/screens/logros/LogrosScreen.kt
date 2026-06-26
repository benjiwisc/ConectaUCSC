package com.ucsc.conectaucsc.ui.screens.logros

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.Attainment
import com.ucsc.conectaucsc.ui.viewmodel.LogrosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(
    navController: NavController,
    recordId: Int,
    logrosViewModel: LogrosViewModel
) {
    val context = LocalContext.current
    val logros by logrosViewModel.logros.collectAsState()
    val isLoading by logrosViewModel.isLoading.collectAsState()
    val mensaje by logrosViewModel.mensaje.collectAsState()

    LaunchedEffect(recordId) {
        logrosViewModel.cargarLogros(recordId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            logrosViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logros y Metas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
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

            if (logros.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay logros asociados a esta asignatura", fontSize = 16.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(logros) { logro ->
                        CardLogroItem(logro = logro)
                    }
                }
            }
        }
    }
}

@Composable
fun CardLogroItem(logro: Attainment) {

    val esCumplido = logro.cumplido == 1

    val porcentajeProgreso = if (logro.meta > 0) {
        (logro.progreso.toFloat() / logro.meta.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esCumplido) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = logro.tipo.replace("_", " ").replaceFirstChar { it.uppercase() },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Meta de la evaluación",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (esCumplido) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Cumplido",
                        tint = Color(0xFF2E7D32), 
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "En progreso",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }


            LinearProgressIndicator(
                progress = { porcentajeProgreso },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (esCumplido) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progreso: ${logro.progreso}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Objetivo: ${logro.meta}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}