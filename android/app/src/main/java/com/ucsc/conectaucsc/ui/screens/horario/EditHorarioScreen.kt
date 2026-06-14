package com.ucsc.conectaucsc.ui.screens.horario

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.Schedule
import com.ucsc.conectaucsc.ui.viewmodel.HorariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHorarioScreen(
    navController: NavController,
    recordId: Int,
    horarioId: Int,
    horariosViewModel: HorariosViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val isLoading by horariosViewModel.isLoading.collectAsState()
    val mensaje by horariosViewModel.mensaje.collectAsState()
    val horarioServer by horariosViewModel.horarioDetalle.collectAsState()

    var diaSeleccionado by remember(horarioServer) { mutableStateOf(horarioServer?.dia ?: "") }
    var tipoSeleccionado by remember(horarioServer) { mutableStateOf(horarioServer?.tipo_clase ?: "") }
    var horaInicio by remember(horarioServer) { mutableStateOf(horarioServer?.hora_inicio?.take(5) ?: "") }
    var horaFin by remember(horarioServer) { mutableStateOf(horarioServer?.hora_fin?.take(5) ?: "") }
    var sala by remember(horarioServer) { mutableStateOf(horarioServer?.sala ?: "") }

    var diaExpanded by remember { mutableStateOf(false) }
    var tipoExpanded by remember { mutableStateOf(false) }

    val dias = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado")
    val tiposClase = listOf("catedra", "ayudantia", "laboratorio")

    LaunchedEffect(recordId, horarioId) {
        horariosViewModel.editHorarios(recordId, horarioId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            horariosViewModel.limpiarMensaje()

            if (it.contains("éxito", true) || it.contains("actualizado", true) || it.contains("correcto", true)) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Horario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (horarioServer == null && isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Modificar Horario #${horarioId}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // --- Dropdown de Día ---
                ExposedDropdownMenuBox(
                    expanded = diaExpanded,
                    onExpandedChange = { diaExpanded = it }
                ) {
                    OutlinedTextField(
                        value = diaSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Día") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diaExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable) // 👈 Actualizado
                    )
                    ExposedDropdownMenu(expanded = diaExpanded, onDismissRequest = { diaExpanded = false }) {
                        dias.forEach { dia ->
                            DropdownMenuItem(
                                text = { Text(dia.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    diaSeleccionado = dia
                                    diaExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    label = { Text("Hora inicio (08:10)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { horaFin = it },
                    label = { Text("Hora fin (09:30)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Dropdown de Tipo de Clase ---
                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { tipoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de clase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable) // 👈 Actualizado
                    )
                    ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false }) {
                        tiposClase.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    tipoExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = sala,
                    onValueChange = { sala = it },
                    label = { Text("Sala (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (diaSeleccionado.isBlank() || horaInicio.isBlank() || horaFin.isBlank() || tipoSeleccionado.isBlank()) {
                            Toast.makeText(context, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val horarioActualizado = Schedule(
                            id = horarioId,
                            record_id = horarioServer?.record_id ?: recordId,
                            dia = diaSeleccionado,
                            hora_inicio = horaInicio,
                            hora_fin = horaFin,
                            tipo_clase = tipoSeleccionado,
                            sala = sala.ifBlank { null }
                        )

                        horariosViewModel.updateHorario(horarioId, horarioActualizado)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("Guardar Cambios")
                    }
                }

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Cancelar")
                }
            }
        }
    }
}