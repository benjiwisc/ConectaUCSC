package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.AlternativeDto
import com.ucsc.conectaucsc.data.model.CreatePracticalEvaluationRequest
import com.ucsc.conectaucsc.data.model.QuestionDto
import com.ucsc.conectaucsc.ui.viewmodel.EvaluacionesPracticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCuestionarioScreen(
    navController: NavController,
    materiaId: Int,
    viewModel: EvaluacionesPracticasViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val preguntas = remember { mutableStateListOf<QuestionData>() }

    val mensaje by viewModel.mensaje.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (it.contains("éxito") || it.contains("correctamente")) {
                navController.popBackStack()
            }
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Cuestionario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Preguntas", style = MaterialTheme.typography.titleLarge)
                }

                itemsIndexed(preguntas) { qIndex, pregunta ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Pregunta ${qIndex + 1}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                IconButton(onClick = { preguntas.removeAt(qIndex) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            OutlinedTextField(
                                value = pregunta.enunciado,
                                onValueChange = { pregunta.enunciado = it },
                                label = { Text("Enunciado") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            pregunta.alternativas.forEachIndexed { aIndex, alternativa ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = pregunta.correctaIndex == aIndex,
                                        onClick = { pregunta.correctaIndex = aIndex }
                                    )
                                    OutlinedTextField(
                                        value = alternativa.texto,
                                        onValueChange = { alternativa.texto = it },
                                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                        placeholder = { Text("Opción ${aIndex + 1}") }
                                    )
                                }
                            }
                            TextButton(onClick = { pregunta.alternativas.add(AlternativeData()) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("Añadir Opción")
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { preguntas.add(QuestionData()) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Añadir Pregunta")
                    }
                }
            }

            Button(
                onClick = {
                    val request = CreatePracticalEvaluationRequest(
                        titulo = titulo,
                        descripcion = descripcion,
                        preguntas = preguntas.map { q ->
                            QuestionDto(
                                enunciado = q.enunciado,
                                alternativas = q.alternativas.mapIndexed { i, a ->
                                    AlternativeDto(texto = a.texto, correcta = i == q.correctaIndex)
                                }
                            )
                        },
                        tipo = "quiz"
                    )
                    viewModel.crearCuestionario(materiaId, request)
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = !isLoading && titulo.isNotBlank() && preguntas.isNotEmpty()
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(24.dp))
                else Text("Guardar Cuestionario")
            }
        }
    }
}

class QuestionData {
    var enunciado by mutableStateOf("")
    val alternativas = mutableStateListOf(AlternativeData(), AlternativeData())
    var correctaIndex by mutableStateOf(0)
}

class AlternativeData {
    var texto by mutableStateOf("")
}