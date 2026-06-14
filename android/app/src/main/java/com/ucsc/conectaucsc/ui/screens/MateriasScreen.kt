package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import com.ucsc.conectaucsc.ui.viewmodel.MateriaViewModel
import com.ucsc.conectaucsc.ui.navigation.Sesiones

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.ucsc.conectaucsc.ui.navigation.RegistroMateria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateriasScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    materiaViewModel: MateriaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val misMaterias by materiaViewModel.misMaterias.collectAsState()
    val materiasDisponibles by materiaViewModel.materiasDisponibles.collectAsState()
    val isLoading by materiaViewModel.isLoading.collectAsState()
    val mensaje by materiaViewModel.mensaje.collectAsState()

    var mostrarDialog by remember { mutableStateOf(false) }
    var materiaSeleccionada by remember { mutableStateOf<com.ucsc.conectaucsc.data.model.Materia?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Cargar materias del usuario al entrar
    LaunchedEffect(Unit) {
        materiaViewModel.cargarMisMaterias()
        // Cargar materias disponibles según la carrera del usuario
        // Por ahora cargamos con carrera_id hardcodeado, luego lo conectamos al perfil
    }

    // Mostrar mensajes
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            materiaViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Materias") },
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
                // Cargar materias disponibles de la carrera del usuario
                val carreraId = authViewModel.getCarreraId()
                if (carreraId != null) {
                    materiaViewModel.cargarMateriasDisponibles(carreraId)
                    mostrarDialog = true
                } else {
                    Toast.makeText(context, "Debes tener una carrera asignada", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar materia")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (misMaterias.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes materias registradas.\nPresiona + para agregar.", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
            ) {
                items(misMaterias) { materia ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        onClick = { navController.navigate(RegistroMateria(materia.id_registro,materia.id, materia.nombre)) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(materia.nombre, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            IconButton(onClick = { materiaViewModel.eliminarMateria(materia.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para agregar materia
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            title = { Text("Agregar Materia") },
            text = {
                Column {
                    Text("Selecciona una materia:", modifier = Modifier.padding(bottom = 8.dp))
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = materiaSeleccionada?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Materia") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            materiasDisponibles.forEach { materia ->
                                DropdownMenuItem(
                                    text = { Text(materia.nombre) },
                                    onClick = {
                                        materiaSeleccionada = materia
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    materiaSeleccionada?.let {
                        materiaViewModel.agregarMateria(it.id)
                        mostrarDialog = false
                        materiaSeleccionada = null
                    }
                }) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialog = false }) { Text("Cancelar") }
            }
        )
    }
}