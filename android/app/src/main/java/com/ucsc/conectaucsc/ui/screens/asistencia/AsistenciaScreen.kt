package com.ucsc.conectaucsc.ui.screens.asistencia

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = asistencia.fecha,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = "Estado: ${asistencia.estado}",
                                    fontSize = 15.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(16.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {

                                    OutlinedButton(
                                        onClick = {
                                            AsistenciasViewModel.justificarAsistencia(
                                                asistencia.id,
                                                asistencia.record_id
                                            )
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }

                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            AsistenciasViewModel.deleteAsistencia(
                                                asistencia.id,
                                                asistencia.record_id
                                            )
                                        }
                                    ) {
                                        Text("Eliminar")
                                    }
                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            AsistenciasViewModel.updateAsistencia(
                                                asistencia.id,
                                                asistencia.record_id
                                            )
                                        }
                                    ) {
                                        Text("Editar")
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