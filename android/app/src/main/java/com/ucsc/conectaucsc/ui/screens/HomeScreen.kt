package com.ucsc.conectaucsc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ucsc.conectaucsc.ui.navigation.Login
import com.ucsc.conectaucsc.ui.navigation.Materias
import com.ucsc.conectaucsc.ui.navigation.TodasSesiones
import com.ucsc.conectaucsc.ui.navigation.Profile
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import com.ucsc.conectaucsc.ui.viewmodel.SesionViewModel
import com.ucsc.conectaucsc.ui.viewmodel.HorariosViewModel
import com.ucsc.conectaucsc.data.model.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    sesionViewModel: SesionViewModel = hiltViewModel(),
    horariosViewModel: HorariosViewModel = hiltViewModel()
) {
    val sesiones by sesionViewModel.sesiones.collectAsState()
    val todosHorarios by horariosViewModel.todosHorarios.collectAsState()

    val todayString = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    }
    val todayDayOfWeek = remember {
        val calendar = java.util.Calendar.getInstance()
        when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SUNDAY -> "domingo"
            java.util.Calendar.MONDAY -> "lunes"
            java.util.Calendar.TUESDAY -> "martes"
            java.util.Calendar.WEDNESDAY -> "miércoles"
            java.util.Calendar.THURSDAY -> "jueves"
            java.util.Calendar.FRIDAY -> "viernes"
            java.util.Calendar.SATURDAY -> "sábado"
            else -> ""
        }
    }
    val currentUserId = remember { viewModel.getUserId() }

    val todaySesiones = remember(sesiones, currentUserId) {
        sesiones.filter { sesion ->
            sesion.fecha_hora.startsWith(todayString) && (
                sesion.user_id == currentUserId ||
                sesion.participantes?.any { it.id == currentUserId } == true
            )
        }
    }

    val todaySchedules = remember(todosHorarios, todayDayOfWeek) {
        todosHorarios.filter { it.dia.lowercase() == todayDayOfWeek }
    }

    
    val unifiedAgenda = remember(todaySesiones, todaySchedules, currentUserId) {
        val sessionsItems = todaySesiones.map { sesion ->
            val hora = if (sesion.fecha_hora.length >= 16) {
                sesion.fecha_hora.substringAfter(" ").substring(0, 5)
            } else {
                "00:00"
            }
            val subtipo = if (sesion.user_id == currentUserId) "Organizador" else "Participante"
            AgendaItem(
                id = sesion.id,
                titulo = sesion.titulo,
                hora = hora,
                materiaNombre = sesion.materia?.nombre ?: "Tutoría",
                lugar = sesion.lugar,
                tipo = "sesion",
                subtipo = subtipo,
                originalObject = sesion
            )
        }

        val classItems = todaySchedules.map { schedule ->
            val hora = if (schedule.hora_inicio.length >= 5) {
                schedule.hora_inicio.substring(0, 5)
            } else {
                "00:00"
            }
            val subtipo = when (schedule.tipo_clase.lowercase()) {
                "catedra" -> "Cátedra"
                "laboratorio" -> "Laboratorio"
                "ayudantia" -> "Ayudantía"
                else -> schedule.tipo_clase.replaceFirstChar { it.uppercase() }
            }
            val matNombre = schedule.record?.usuario_materia?.materia?.nombre ?: "Clase"
            AgendaItem(
                id = schedule.id,
                titulo = "${subtipo} de $matNombre",
                hora = hora,
                materiaNombre = matNombre,
                lugar = schedule.sala ?: "Por definir",
                tipo = "clase",
                subtipo = subtipo,
                originalObject = schedule
            )
        }

        (sessionsItems + classItems).sortedBy { it.hora }
    }

    
    LaunchedEffect(Unit) {
        sesionViewModel.cargarTodasSesiones()
        horariosViewModel.cargarTodosHorarios()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp, top = 8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesión"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar sesión",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Bienvenido de nuevo,",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = viewModel.getUserName() ?: "Estudiante",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Portal Universitario ConectaUCSC",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        title = "Mis Materias",
                        description = "Tus ramos y rendimiento",
                        icon = Icons.Default.School,
                        onClick = { navController.navigate(Materias) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 135.dp)
                            .fillMaxHeight()
                    )
                    FeatureCard(
                        title = "Sesiones de Estudio",
                        description = "Tutorías y repasos",
                        icon = Icons.Default.Groups,
                        onClick = { navController.navigate(TodasSesiones) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 135.dp)
                            .fillMaxHeight()
                    )
                }

                
                FeatureCard(
                    title = "Mi Perfil",
                    description = "Administra la información de tu carrera y cuenta",
                    icon = Icons.Default.AccountCircle,
                    onClick = { navController.navigate(Profile) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                )
            }

            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hoy en tu Agenda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (unifiedAgenda.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "No tienes clases ni tutorías programadas para hoy. ¡Disfruta tu día! ☕",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        unifiedAgenda.forEach { item ->
                            val badgeColor = when (item.tipo) {
                                "clase" -> MaterialTheme.colorScheme.tertiaryContainer
                                "sesion" -> if (item.subtipo == "Organizador") {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val badgeTextColor = when (item.tipo) {
                                "clase" -> MaterialTheme.colorScheme.onTertiaryContainer
                                "sesion" -> if (item.subtipo == "Organizador") {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                }
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .fillMaxHeight()
                                            .background(badgeColor)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(
                                                    color = badgeColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (item.tipo == "clase") Icons.Default.School else Icons.Default.Groups,
                                                contentDescription = null,
                                                tint = badgeColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = item.titulo,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                AgendaBadge(
                                                    text = item.subtipo ?: "",
                                                    containerColor = badgeColor,
                                                    contentColor = badgeTextColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Schedule,
                                                        contentDescription = "Hora",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = item.hora,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                
                                                val detailText = if (item.tipo == "clase") item.lugar else item.materiaNombre
                                                if (!detailText.isNullOrBlank()) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (item.tipo == "clase") Icons.Default.Place else Icons.Default.Info,
                                                            contentDescription = if (item.tipo == "clase") "Lugar" else "Información",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                        Text(
                                                            text = detailText,
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AgendaBadge(
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

data class AgendaItem(
    val id: Int,
    val titulo: String,
    val hora: String,
    val materiaNombre: String,
    val lugar: String,
    val tipo: String, 
    val subtipo: String?, 
    val originalObject: Any
)