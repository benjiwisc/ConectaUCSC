package com.ucsc.conectaucsc.ui.screens.horario

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.ucsc.conectaucsc.ui.navigation.AddHorario
import com.ucsc.conectaucsc.ui.navigation.EditHorario
import com.ucsc.conectaucsc.ui.viewmodel.HorariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioScreen(
    navController: NavController,
    recordId: Int,
    horariosViewModel: HorariosViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val horarios by horariosViewModel.horarios.collectAsState()
    val isLoading by horariosViewModel.isLoading.collectAsState()
    val mensaje by horariosViewModel.mensaje.collectAsState()

    LaunchedEffect(recordId) {
        horariosViewModel.cargarHorarios(recordId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            horariosViewModel.limpiarMensaje()
        }
    }

    val ordenDias = mapOf(
        "Lunes" to 1,
        "Martes" to 2,
        "Miércoles" to 3,
        "Miercoles" to 3,
        "Jueves" to 4,
        "Viernes" to 5,
        "Sábado" to 6,
        "Domingo" to 7
    )

    val horariosOrdenados = horarios.sortedBy {
        ordenDias[it.dia] ?: 99
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Horario") },
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
        },
        bottomBar = {
            BottomAppBar {
                IconButton(
                    onClick = {
                        navController.navigate(AddHorario(recordId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar"
                    )
                }
                Text("Agregar")
            }
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

            horariosOrdenados.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No existen horarios registrados")
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {


                    items(horariosOrdenados) { horario ->

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
                                    text = horario.dia,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = "${horario.hora_inicio} - ${horario.hora_fin}"
                                )

                                Text(
                                    text = "Tipo: ${horario.tipo_clase}"
                                )

                                horario.sala?.let {
                                    Text(
                                        text = "Sala: $it"
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(16.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {

                                    OutlinedButton(
                                        onClick = {
                                            navController.navigate(
                                                EditHorario(
                                                    horario.record_id,
                                                    horario.id
                                                )
                                            )

                                        }
                                    ) {
                                        Text("Editar")
                                    }

                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            horariosViewModel.deleteHorario(
                                                horario.id,
                                                horario.record_id,
                                            )
                                        }
                                    ) {
                                        Text("Eliminar")
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