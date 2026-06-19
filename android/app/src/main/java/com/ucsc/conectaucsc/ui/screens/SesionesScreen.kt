package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
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
import com.ucsc.conectaucsc.data.model.SesionEstudio
import com.ucsc.conectaucsc.ui.navigation.Chat
import com.ucsc.conectaucsc.ui.navigation.CrearSesion
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import com.ucsc.conectaucsc.ui.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionesScreen(
    materiaId: Int,
    materiaNombre: String,
    navController: NavController,
    authViewModel: AuthViewModel,
    sesionViewModel: SesionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sesiones by sesionViewModel.sesiones.collectAsState()
    val isLoading by sesionViewModel.isLoading.collectAsState()
    val mensaje by sesionViewModel.mensaje.collectAsState()
    val userId = authViewModel.getUserId()

    LaunchedEffect(Unit) {
        sesionViewModel.cargarSesionesPorMateria(materiaId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            sesionViewModel.limpiarMensaje()
            sesionViewModel.cargarSesionesPorMateria(materiaId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(materiaNombre) },
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
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(CrearSesion(materiaId, materiaNombre))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Crear sesión")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (sesiones.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay sesiones para esta materia.\nPresiona + para crear una.",
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                items(sesiones) { sesion ->
                    SesionCard(
                        sesion = sesion,
                        userId = userId,
                        materiaId = materiaId,
                        onUnirse = { sesionViewModel.unirse(sesion.id, materiaId) },
                        onSalirse = { sesionViewModel.salirse(sesion.id, materiaId) },
                        onFinalizar = { sesionViewModel.finalizar(sesion.id, materiaId) },
                        onChatClick = {
                            navController.navigate(Chat(sesion.id, sesion.titulo))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SesionCard(
    sesion: SesionEstudio,
    userId: Int,
    materiaId: Int,
    onUnirse: () -> Unit,
    onSalirse: () -> Unit,
    onFinalizar: () -> Unit,
    onChatClick: () -> Unit
) {
    val yaParticipa = sesion.participantes?.any { it.id == userId } == true
    val esCreador = sesion.user_id == userId

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            sesion.materia?.let {
                Text(
                    text = it.nombre.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = sesion.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lugar: ${sesion.lugar}",
                fontSize = 14.sp
            )
            Text(
                text = "Fecha: ${sesion.fecha_hora}",
                fontSize = 14.sp
            )
            sesion.descripcion?.let {
                Text(
                    text = "Descripcion: $it",
                    fontSize = 14.sp
                )
            }
            Text(
                text = "Creado por: ${sesion.creador?.name ?: "Desconocido"}",
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Participantes: ${sesion.participantes?.size ?: 0}",
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // El botón de chat se muestra a la izquierda si es creador o ya participa
                if (yaParticipa || esCreador) {
                    OutlinedButton(
                        onClick = onChatClick,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat", fontSize = 12.sp)
                    }
                }

                val actionButtonWeight = if (yaParticipa || esCreador) 1f else 2f
                when {
                    esCreador -> {
                        OutlinedButton(
                            onClick = onFinalizar,
                            modifier = Modifier.weight(actionButtonWeight),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Finalizar", fontSize = 12.sp)
                        }
                    }
                    yaParticipa -> {
                        OutlinedButton(
                            onClick = onSalirse,
                            modifier = Modifier.weight(actionButtonWeight),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Salir", fontSize = 12.sp)
                        }
                    }
                    else -> {
                        Button(
                            onClick = onUnirse,
                            modifier = Modifier.weight(actionButtonWeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Unirse", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}