package com.ucsc.conectaucsc.ui.screens.notas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.Grade
import com.ucsc.conectaucsc.ui.viewmodel.NotasViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNotasScreen(
    navController: NavController,
    recordId: Int,
    notaId: Int,
    notasViewModel: NotasViewModel
) {
    val context = LocalContext.current
    val isLoading by notasViewModel.isLoading.collectAsState()
    val mensaje by notasViewModel.mensaje.collectAsState()
    val notaServer by notasViewModel.notaDetalle.collectAsState()

    var evaluacion by remember(notaServer) { mutableStateOf(notaServer?.evaluacion ?: "") }
    var notaInput by remember(notaServer) { mutableStateOf(notaServer?.nota?.toString() ?: "") }
    var porcentajeInput by remember(notaServer) { mutableStateOf(notaServer?.porcentaje?.toString() ?: "") }

    LaunchedEffect(recordId, notaId) {
        notasViewModel.editarNota(recordId, notaId)
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            notasViewModel.limpiarMensaje()
            if (it.contains("éxito", true) || it.contains("actualizada", true)) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modificar Nota") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
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
            if (isLoading && notaServer == null) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(
                value = evaluacion,
                onValueChange = { evaluacion = it },
                label = { Text("Evaluación") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notaInput,
                onValueChange = { notaInput = it },
                label = { Text("Nota") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = porcentajeInput,
                onValueChange = { porcentajeInput = it },
                label = { Text("Porcentaje (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val notaDouble = notaInput.toDoubleOrNull()
                    val porcentajeInt = porcentajeInput.toIntOrNull()

                    if (evaluacion.isBlank() || notaDouble == null || porcentajeInt == null) {
                        Toast.makeText(context, "Los campos son obligatorios o inválidos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val nuevaNota = Grade(
                        id = 0,
                        record_id = recordId,
                        evaluacion = evaluacion,
                        nota = notaDouble,
                        porcentaje = porcentajeInt
                    )

                    notasViewModel.updateNota(notaId, nuevaNota)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator() else Text("Guardar Cambios")
            }
        }
    }
}