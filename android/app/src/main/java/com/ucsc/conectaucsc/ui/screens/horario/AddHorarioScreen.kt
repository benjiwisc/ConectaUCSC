package com.ucsc.conectaucsc.ui.screens.horario

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHorarioScreen(
    navController: NavController,
    recordId: Int,
    horariosViewModel: HorariosViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val isLoading by horariosViewModel.isLoading.collectAsState()
    val mensaje by horariosViewModel.mensaje.collectAsState()

    var diaSeleccionado by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf("") }

    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var sala by remember { mutableStateOf("") }

    var diaExpanded by remember { mutableStateOf(false) }
    var tipoExpanded by remember { mutableStateOf(false) }

    var showInicioTimePicker by remember { mutableStateOf(false) }
    var showFinTimePicker by remember { mutableStateOf(false) }

    val dias = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado")
    val tiposClase = listOf("catedra", "ayudantia", "laboratorio")

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            horariosViewModel.limpiarMensaje()

            if (it.contains("éxito", true) || it.contains("creado", true)) {
                navController.popBackStack()
            }
        }
    }

    if (showInicioTimePicker) {
        val calendar = Calendar.getInstance()
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showInicioTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    horaInicio = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showInicioTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showInicioTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    if (showFinTimePicker) {
        val calendar = Calendar.getInstance()
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showFinTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    horaFin = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showFinTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showFinTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Horario") },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(text = "Nuevo Horario", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            ExposedDropdownMenuBox(
                expanded = diaExpanded,
                onExpandedChange = { diaExpanded = it }
            ) {
                OutlinedTextField(
                    value = diaSeleccionado.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Día") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
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

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora inicio") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors() // Mantiene colores normales
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showInicioTimePicker = true }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora fin") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showFinTimePicker = true }
                )
            }

            ExposedDropdownMenuBox(
                expanded = tipoExpanded,
                onExpandedChange = { tipoExpanded = it }
            ) {
                OutlinedTextField(
                    value = tipoSeleccionado.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de clase") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
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

                    val nuevoHorario = Schedule(
                        record_id = recordId,
                        dia = diaSeleccionado,
                        hora_inicio = horaInicio,
                        hora_fin = horaFin,
                        tipo_clase = tipoSeleccionado,
                        sala = if (sala.isNotBlank()) sala else null
                    )
                    horariosViewModel.createHorario(nuevoHorario)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator() else Text("Guardar Horario")
            }

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Cancelar")
            }
        }
    }
}