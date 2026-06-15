package com.ucsc.conectaucsc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ucsc.conectaucsc.ui.screens.*
import com.ucsc.conectaucsc.ui.screens.asistencia.AsistenciaScreen
import com.ucsc.conectaucsc.ui.screens.horario.AddHorarioScreen
import com.ucsc.conectaucsc.ui.screens.horario.EditHorarioScreen
import com.ucsc.conectaucsc.ui.screens.horario.HorarioScreen
import com.ucsc.conectaucsc.ui.screens.logros.LogrosScreen
import com.ucsc.conectaucsc.ui.screens.notas.AddNotasScreen
import com.ucsc.conectaucsc.ui.screens.notas.EditNotasScreen
import com.ucsc.conectaucsc.ui.screens.notas.NotasScreen
import com.ucsc.conectaucsc.ui.viewmodel.*
import kotlinx.serialization.Serializable

@Serializable object Login
@Serializable object Register
@Serializable object Home
@Serializable object Materias

@Serializable
data class RegistroMateria(val registroId: Int, val materiaId: Int, val materiaNombre: String)

@Serializable data class Asistencia(val registroId: Int)
@Serializable data class Logros(val registroId: Int)
@Serializable data class Horario(val registroId: Int)
@Serializable data class AddHorario(val registroId: Int)
@Serializable data class EditHorario(val registroId: Int, val horarioId: Int)
@Serializable data class Notas(val registroId: Int)
@Serializable data class AddNotas(val registroId: Int)
@Serializable data class EditNotas(val registroId: Int, val notaId: Int)
@Serializable data class Sesiones(val materiaId: Int, val materiaNombre: String)
@Serializable data class CrearSesion(val materiaId: Int, val materiaNombre: String)
@Serializable data class EvaluacionesPracticas(val materiaId: Int)
@Serializable data class ArchivosEstudio(val materiaId: Int)

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val viewModel_asistencia: AsistenciasViewModel = viewModel()
    val viewModel_horario: HorariosViewModel = viewModel()
    val viewModel_logros: LogrosViewModel = viewModel()
    val viewModel_notas: NotasViewModel = viewModel()

    val startDestination = if (viewModel.isLoggedIn()) Home else Login

    NavHost(navController = navController, startDestination = startDestination) {
        composable<Login> {
            LoginScreen(viewModel = viewModel, navController = navController)
        }
        composable<Register> {
            RegisterScreen(viewModel = viewModel, navController = navController)
        }
        composable<Home> {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable<Materias> {
            MateriasScreen(navController = navController, authViewModel = viewModel)
        }
        composable<RegistroMateria> { backStackEntry ->
            val route: RegistroMateria = backStackEntry.toRoute()
            RegistroMateriaScreen(
                navController = navController,
                route.registroId,
                route.materiaId,
                route.materiaNombre
            )
        }
        composable<Asistencia> { backStackEntry ->
            val route: Asistencia = backStackEntry.toRoute()
            AsistenciaScreen(navController = navController, route.registroId, viewModel_asistencia)
        }
        composable<Horario> { backStackEntry ->
            val route: Horario = backStackEntry.toRoute()
            HorarioScreen(navController = navController, route.registroId, viewModel_horario)
        }
        composable<AddHorario> { backStackEntry ->
            val route: AddHorario = backStackEntry.toRoute()
            AddHorarioScreen(navController = navController, route.registroId, viewModel_horario)
        }
        composable<EditHorario> { backStackEntry ->
            val route: EditHorario = backStackEntry.toRoute()
            EditHorarioScreen(navController = navController, route.registroId, route.horarioId, viewModel_horario)
        }
        composable<Notas> { backStackEntry ->
            val route: Notas = backStackEntry.toRoute()
            NotasScreen(navController = navController, route.registroId, viewModel_notas)
        }
        composable<AddNotas> { backStackEntry ->
            val route: AddNotas = backStackEntry.toRoute()
            AddNotasScreen(navController = navController, route.registroId, viewModel_notas)
        }
        composable<EditNotas> { backStackEntry ->
            val route: EditNotas = backStackEntry.toRoute()
            EditNotasScreen(navController = navController, route.registroId, route.notaId, viewModel_notas)
        }
        composable<Logros> { backStackEntry ->
            val route: Logros = backStackEntry.toRoute()
            LogrosScreen(navController = navController, route.registroId, viewModel_logros)
        }
        composable<Sesiones> { backStackEntry ->
            val route: Sesiones = backStackEntry.toRoute()
            SesionesScreen(
                materiaId = route.materiaId,
                materiaNombre = route.materiaNombre,
                navController = navController,
                authViewModel = viewModel
            )
        }
        composable<CrearSesion> { backStackEntry ->
            val route: CrearSesion = backStackEntry.toRoute()
            CrearSesionScreen(
                materiaId = route.materiaId,
                materiaNombre = route.materiaNombre,
                navController = navController
            )
        }
        composable<EvaluacionesPracticas> { backStackEntry ->
            val route: EvaluacionesPracticas = backStackEntry.toRoute()
            EvaluacionesPracticasScreen(
                navController = navController,
                materiaId = route.materiaId
            )
        }
        composable<ArchivosEstudio> { backStackEntry ->
            val route: ArchivosEstudio = backStackEntry.toRoute()
            ArchivosEstudioScreen(
                navController = navController,
                materiaId = route.materiaId
            )
        }
    }
}