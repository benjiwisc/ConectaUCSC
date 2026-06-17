package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.data.model.PracticalEvaluationDto
import com.ucsc.conectaucsc.data.model.QuestionDto
import com.ucsc.conectaucsc.ui.viewmodel.EvaluacionesPracticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCuestionarioScreen(
    navController: NavController,
    evaluationId: Int,
    viewModel: EvaluacionesPracticasViewModel = hiltViewModel()
) {
    val evaluation by viewModel.evaluationActual.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val context = LocalContext.current
    
    val respuestasUsuario = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(evaluationId) {
        viewModel.cargarDetalleEvaluacion(evaluationId)
    }

    // Mostrar mensajes del servidor (errores o éxito)
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(evaluation?.titulo ?: "Cuestionario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && evaluation == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (evaluation != null) {
                val eval = evaluation!!
                
                // Si ya tiene nota o está marcada como hecha, mostramos resultados
                if (eval.hecha || eval.nota != null || eval.completion != null) {
                    ResultadosView(eval)
                } else {
                    CuestionarioView(
                        preguntas = eval.contenido ?: emptyList(),
                        respuestasUsuario = respuestasUsuario,
                        isLoading = isLoading,
                        onEnviar = {
                            val listaRespuestas = (0 until (eval.contenido?.size ?: 0)).map { 
                                respuestasUsuario[it] ?: -1 
                            }
                            if (!listaRespuestas.contains(-1)) {
                                viewModel.enviarRespuestas(evaluationId, listaRespuestas)
                            } else {
                                Toast.makeText(context, "Por favor responde todas las preguntas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
            
            // Overlay de carga al enviar
            if (isLoading && evaluation != null) {
                Surface(
                    color = Color.Black.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun CuestionarioView(
    preguntas: List<QuestionDto>,
    respuestasUsuario: MutableMap<Int, Int>,
    isLoading: Boolean,
    onEnviar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(preguntas) { qIndex, pregunta ->
                Text(
                    text = "${qIndex + 1}. ${pregunta.enunciado}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                pregunta.alternativas.forEachIndexed { aIndex, alternativa ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (respuestasUsuario[qIndex] == aIndex),
                                onClick = { if (!isLoading) respuestasUsuario[qIndex] = aIndex }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (respuestasUsuario[qIndex] == aIndex),
                            onClick = { if (!isLoading) respuestasUsuario[qIndex] = aIndex },
                            enabled = !isLoading
                        )
                        Text(
                            text = alternativa.texto, 
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (isLoading) Color.Gray else Color.Unspecified
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
        
        Button(
            onClick = onEnviar,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = respuestasUsuario.size == preguntas.size && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Enviar Respuestas")
            }
        }
    }
}

@Composable
fun ResultadosView(eval: PracticalEvaluationDto) {
    // Usamos los campos del objeto directamente si completion es nulo
    val nota = eval.completion?.nota ?: eval.nota ?: 0.0
    val correctas = eval.completion?.correctas ?: eval.correctas ?: 0
    val total = eval.completion?.total_preguntas ?: eval.total_preguntas ?: (eval.contenido?.size ?: 0)
    val puntaje = eval.completion?.puntaje ?: eval.puntaje ?: 0
    val respuestasDadas = eval.completion?.respuestas ?: emptyList<Int>()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Resultado Final", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = nota.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Correctas: $correctas / $total",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Puntaje: $puntaje%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Resumen de la evaluación", 
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(eval.contenido ?: emptyList()) { qIndex, pregunta ->
                val respuestaElegidaIdx = respuestasDadas.getOrNull(qIndex)
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "${qIndex + 1}. ${pregunta.enunciado}", fontWeight = FontWeight.Bold)
                        
                        pregunta.alternativas.forEachIndexed { aIndex, alternativa ->
                            val isSelected = respuestaElegidaIdx == aIndex
                            val isCorrect = alternativa.correcta == true
                            
                            val textColor = when {
                                isCorrect -> Color(0xFF4CAF50) // Verde: Es la correcta
                                isSelected && !isCorrect -> MaterialTheme.colorScheme.error // Rojo: Elegiste esta y está mal
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSelected, onClick = null, enabled = false)
                                Text(
                                    text = alternativa.texto,
                                    color = textColor,
                                    fontWeight = if (isSelected || isCorrect) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
