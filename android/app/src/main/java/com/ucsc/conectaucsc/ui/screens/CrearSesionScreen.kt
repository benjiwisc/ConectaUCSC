package com.ucsc.conectaucsc.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.ui.viewmodel.SesionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSesionScreen(
    materiaId: Int,
    materiaNombre: String,
    navController: NavController,
    sesionViewModel: SesionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoading by sesionViewModel.isLoading.collectAsState()
    val mensaje by sesionViewModel.mensaje.collectAsState()
    val sesionCreada by sesionViewModel.sesionCreada.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaHora by remember { mutableStateOf("") }

    LaunchedEffect(sesionCreada) {
        if (sesionCreada) {
            sesionViewModel.limpiarSesionCreada()
            navController.popBackStack()
        }
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            sesionViewModel.limpiarMensaje()
        }
    }

    
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val timePicker = TimePickerDialog(
                context,
                { _, hour, minute ->
                    fechaHora = "%04d-%02d-%02d %02d:%02d:00".format(year, month + 1, day, hour, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Sesión") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Materia: $materiaNombre",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la sesión") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = lugar,
                onValueChange = { lugar = it },
                label = { Text("Lugar") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            OutlinedButton(
                onClick = { datePicker.show() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(if (fechaHora.isEmpty()) "Seleccionar fecha y hora" else fechaHora)
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                minLines = 3
            )

            Button(
                onClick = {
                    if (titulo.isEmpty() || lugar.isEmpty() || fechaHora.isEmpty()) {
                        Toast.makeText(context, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    sesionViewModel.crearSesion(
                        titulo, lugar, fechaHora,
                        descripcion.ifEmpty { null },
                        materiaId
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Crear Sesión")
                }
            }
        }
    }
}