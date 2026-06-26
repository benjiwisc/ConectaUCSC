package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import com.ucsc.conectaucsc.ui.navigation.CrearSesion
import com.ucsc.conectaucsc.ui.navigation.Chat
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import com.ucsc.conectaucsc.ui.viewmodel.MateriaViewModel
import com.ucsc.conectaucsc.ui.viewmodel.SesionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodasSesionesScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    sesionViewModel: SesionViewModel = hiltViewModel(),
    materiaViewModel: MateriaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sesiones by sesionViewModel.sesiones.collectAsState()
    val isLoadingSesiones by sesionViewModel.isLoading.collectAsState()
    val mensajeSesion by sesionViewModel.mensaje.collectAsState()
    val userId = authViewModel.getUserId()

    val misMaterias by materiaViewModel.misMaterias.collectAsState()
    val isLoadingMaterias by materiaViewModel.isLoading.collectAsState()
    val mensajeMateria by materiaViewModel.mensaje.collectAsState()

    var buscarText by remember { mutableStateOf("") }
    var ordenSeleccionado by remember { mutableStateOf("antiguos") } 

    
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var showOnlyMySessions by remember { mutableStateOf(false) }
    var showOnlyMyCreatedSessions by remember { mutableStateOf(false) }
    var showOnlyActiveSessions by remember { mutableStateOf(true) }

    var showSubjectDropdown by remember { mutableStateOf(false) }
    var showSubjectSelectorDialog by remember { mutableStateOf(false) }

    
    LaunchedEffect(ordenSeleccionado) {
        sesionViewModel.cargarTodasSesiones(buscar = buscarText.ifEmpty { null }, orden = ordenSeleccionado)
    }

    LaunchedEffect(Unit) {
        materiaViewModel.cargarMisMaterias()
    }

    
    LaunchedEffect(mensajeSesion) {
        mensajeSesion?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            sesionViewModel.limpiarMensaje()
            sesionViewModel.cargarTodasSesiones(buscar = buscarText.ifEmpty { null }, orden = ordenSeleccionado)
        }
    }

    LaunchedEffect(mensajeMateria) {
        mensajeMateria?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            materiaViewModel.limpiarMensaje()
        }
    }

    
    val filteredSesiones = remember(
        sesiones,
        selectedSubjectId,
        showOnlyMySessions,
        showOnlyMyCreatedSessions,
        showOnlyActiveSessions,
        userId
    ) {
        sesiones.filter { sesion ->
            
            val matchesSubject = selectedSubjectId == null || sesion.materia_id == selectedSubjectId

            
            
            
            val matchesCreated = if (showOnlyMyCreatedSessions) {
                sesion.user_id == userId
            } else {
                sesion.user_id != userId
            }

            
            val matchesMySessions = !showOnlyMySessions ||
                    (sesion.participantes?.any { it.id == userId } == true && sesion.user_id != userId)

            
            val matchesActive = !showOnlyActiveSessions || run {
                val currentDateTimeString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val sessionDateTimeClean = sesion.fecha_hora.replace("T", " ").substringBefore(".")
                sessionDateTimeClean >= currentDateTimeString
            }

            matchesSubject && matchesCreated && matchesMySessions && matchesActive
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sesiones de Estudio", fontWeight = FontWeight.Bold) },
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
                val currentSelectedId = selectedSubjectId
                if (currentSelectedId != null) {
                    val materia = misMaterias.find { it.id == currentSelectedId }
                    if (materia != null) {
                        navController.navigate(CrearSesion(materia.id, materia.nombre))
                    }
                } else {
                    showSubjectSelectorDialog = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Crear sesión")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            
            OutlinedTextField(
                value = buscarText,
                onValueChange = {
                    buscarText = it
                    sesionViewModel.cargarTodasSesiones(buscar = it.ifEmpty { null }, orden = ordenSeleccionado)
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                placeholder = { Text("Buscar por ramo o ayudante...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (buscarText.isNotEmpty()) {
                        IconButton(onClick = {
                            buscarText = ""
                            sesionViewModel.cargarTodasSesiones(buscar = null, orden = ordenSeleccionado)
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true
            )

            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                
                item {
                    val selectedSubjectName = misMaterias.find { it.id == selectedSubjectId }?.nombre ?: "Todos los ramos"
                    Box {
                        FilterChip(
                            selected = selectedSubjectId != null,
                            onClick = { showSubjectDropdown = true },
                            label = { Text(selectedSubjectName) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar ramos") }
                        )
                        DropdownMenu(
                            expanded = showSubjectDropdown,
                            onDismissRequest = { showSubjectDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos los ramos") },
                                onClick = {
                                    selectedSubjectId = null
                                    showSubjectDropdown = false
                                }
                            )
                            misMaterias.forEach { materia ->
                                DropdownMenuItem(
                                    text = { Text(materia.nombre) },
                                    onClick = {
                                        selectedSubjectId = materia.id
                                        showSubjectDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                
                item {
                    FilterChip(
                        selected = showOnlyActiveSessions,
                        onClick = { showOnlyActiveSessions = !showOnlyActiveSessions },
                        label = { Text("Sólo activas") }
                    )
                }

                
                item {
                    FilterChip(
                        selected = showOnlyMySessions,
                        onClick = {
                            showOnlyMySessions = !showOnlyMySessions
                            if (showOnlyMySessions) {
                                showOnlyMyCreatedSessions = false
                            }
                        },
                        label = { Text("Mis Inscritas") }
                    )
                }

                
                item {
                    FilterChip(
                        selected = showOnlyMyCreatedSessions,
                        onClick = {
                            showOnlyMyCreatedSessions = !showOnlyMyCreatedSessions
                            if (showOnlyMyCreatedSessions) {
                                showOnlyMySessions = false
                            }
                        },
                        label = { Text("Creadas por mí") }
                    )
                }

                
                item {
                    val labelText = if (ordenSeleccionado == "antiguos") "Más cercanas" else "Más lejanas"
                    FilterChip(
                        selected = true,
                        onClick = {
                            ordenSeleccionado = if (ordenSeleccionado == "antiguos") "recientes" else "antiguos"
                        },
                        label = { Text("Orden: $labelText") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            
            if (isLoadingSesiones || isLoadingMaterias) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredSesiones.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron sesiones de estudio.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSesiones) { sesion ->
                        SesionCard(
                            sesion = sesion,
                            userId = userId,
                            materiaId = sesion.materia_id,
                            onUnirse = {
                                sesionViewModel.unirse(
                                    sesionId = sesion.id,
                                    materiaId = sesion.materia_id,
                                    onGeneral = true,
                                    buscar = buscarText.ifEmpty { null },
                                    orden = ordenSeleccionado
                                )
                            },
                            onSalirse = {
                                sesionViewModel.salirse(
                                    sesionId = sesion.id,
                                    materiaId = sesion.materia_id,
                                    onGeneral = true,
                                    buscar = buscarText.ifEmpty { null },
                                    orden = ordenSeleccionado
                                )
                            },
                            onFinalizar = {
                                sesionViewModel.finalizar(
                                    sesionId = sesion.id,
                                    materiaId = sesion.materia_id,
                                    onGeneral = true,
                                    buscar = buscarText.ifEmpty { null },
                                    orden = ordenSeleccionado
                                )
                            },
                            onChatClick = {
                                navController.navigate(Chat(sesion.id, sesion.titulo))
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSubjectSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showSubjectSelectorDialog = false },
            title = { Text("Selecciona el ramo") },
            text = {
                Column {
                    Text("¿Para qué ramo deseas crear la sesión de estudio?")
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp)) {
                        items(misMaterias) { materia ->
                            TextButton(
                                onClick = {
                                    showSubjectSelectorDialog = false
                                    navController.navigate(CrearSesion(materia.id, materia.nombre))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = materia.nombre,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSubjectSelectorDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
