package com.ucsc.conectaucsc.ui.screens.notas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.Grade
import com.ucsc.conectaucsc.ui.viewmodel.NotasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotasScreen(
    navController: NavController,
    recordId: Int,
    notasViewModel: NotasViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoading by notasViewModel.isLoading.collectAsState()
    val mensaje by notasViewModel.mensaje.collectAsState()

    var evaluacion by remember { mutableStateOf("") }
    var notaInput by remember { mutableStateOf("") }
    var porcentajeInput by remember { mutableStateOf("") }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            notasViewModel.limpiarMensaje()
            if (it.contains("éxito", true)) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nueva Nota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            OutlinedTextField(
                value = evaluacion,
                onValueChange = { evaluacion = it },
                label = { Text("Nombre de la evaluación (Ej: Certamen 1)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notaInput,
                onValueChange = { notaInput = it },
                label = { Text("Nota (Ej: 6.5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = porcentajeInput,
                onValueChange = { porcentajeInput = it },
                label = { Text("Porcentaje (Ej: 25)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val notaDouble = notaInput.toDoubleOrNull()
                    val porcentajeInt = porcentajeInput.toIntOrNull()

                    if (evaluacion.isBlank() || notaDouble == null || porcentajeInt == null) {
                        Toast.makeText(context, "Verifica los datos ingresados", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val nuevaNota = Grade(
                        id = 0,
                        record_id = recordId,
                        evaluacion = evaluacion,
                        nota = notaDouble,
                        porcentaje = porcentajeInt
                    )

                    notasViewModel.createNota(nuevaNota, recordId)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator() else Text("Registrar Nota")
            }
        }
    }
}