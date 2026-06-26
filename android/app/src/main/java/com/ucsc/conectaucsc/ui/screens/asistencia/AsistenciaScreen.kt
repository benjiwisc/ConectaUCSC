package com.ucsc.conectaucsc.ui.screens.asistencia

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.ui.viewmodel.AsistenciasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenciaScreen(
    navController: NavController,
    recordId: Int,
    AsistenciasViewModel: AsistenciasViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val asistencias by AsistenciasViewModel.asistencias.collectAsState()
    val isLoading by AsistenciasViewModel.isLoading.collectAsState()
    val mensaje by AsistenciasViewModel.mensaje.collectAsState()

    LaunchedEffect(recordId) {
        AsistenciasViewModel.cargarAsistencias(recordId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            AsistenciasViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistencias") },
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

        when {

            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            asistencias.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No existen asistencias registradas",
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {

                    items(asistencias) { asistencia ->

                        val isPresente = asistencia.estado.equals("Presente", ignoreCase = true)
                        val statusColor = if (isPresente) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        val statusIcon = if (isPresente) Icons.Default.CheckCircle else Icons.Default.Cancel

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        
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

                                        
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = asistencia.fecha,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Estado: ${asistencia.estado}",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(text = asistencia.estado, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = statusColor.copy(alpha = 0.12f),
                                                labelColor = statusColor
                                            ),
                                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        
                                        OutlinedButton(
                                            onClick = {
                                                AsistenciasViewModel.justificarAsistencia(
                                                    asistencia.id,
                                                    asistencia.record_id
                                                )
                                            },
                                            modifier = Modifier.height(36.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                        ) {
                                            Text("Cancelar", fontSize = 12.sp)
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        
                                        OutlinedButton(
                                            onClick = {
                                                AsistenciasViewModel.updateAsistencia(
                                                    asistencia.id,
                                                    asistencia.record_id
                                                )
                                            },
                                            modifier = Modifier.height(36.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Editar", fontSize = 12.sp)
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        
                                        OutlinedButton(
                                            onClick = {
                                                AsistenciasViewModel.deleteAsistencia(
                                                    asistencia.id,
                                                    asistencia.record_id
                                                )
                                            },
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}