package com.ucsc.conectaucsc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ucsc.conectaucsc.ui.screens.HomeScreen
import com.ucsc.conectaucsc.ui.screens.LoginScreen
import com.ucsc.conectaucsc.ui.screens.RegisterScreen
import com.ucsc.conectaucsc.ui.screens.RegistroMateriaScreen
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import kotlinx.serialization.Serializable
import com.ucsc.conectaucsc.ui.screens.MateriasScreen
import com.ucsc.conectaucsc.ui.screens.asistencia.AsistenciaScreen
import com.ucsc.conectaucsc.ui.screens.horario.AddHorarioScreen
import com.ucsc.conectaucsc.ui.screens.horario.EditHorarioScreen
import com.ucsc.conectaucsc.ui.screens.horario.HorarioScreen
import com.ucsc.conectaucsc.ui.screens.logros.LogrosScreen
import com.ucsc.conectaucsc.ui.screens.notas.AddNotasScreen
import com.ucsc.conectaucsc.ui.screens.notas.EditNotasScreen
import com.ucsc.conectaucsc.ui.screens.notas.NotasScreen
import com.ucsc.conectaucsc.ui.viewmodel.AsistenciasViewModel
import com.ucsc.conectaucsc.ui.viewmodel.HorariosViewModel
import com.ucsc.conectaucsc.ui.viewmodel.LogrosViewModel
import com.ucsc.conectaucsc.ui.viewmodel.NotasViewModel
import com.ucsc.conectaucsc.ui.screens.SesionesScreen
import com.ucsc.conectaucsc.ui.screens.CrearSesionScreen
import androidx.navigation.toRoute

@Serializable object Login
@Serializable object Register
@Serializable object Home

@Serializable object Materias
@Serializable
data class RegistroMateria(val registroId: Int)
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
            val RegistroMateria: RegistroMateria = backStackEntry.toRoute()
            RegistroMateriaScreen(navController = navController, RegistroMateria.registroId)
        }
        composable<Asistencia> { backStackEntry ->
            val Asistencia: Asistencia = backStackEntry.toRoute()
            AsistenciaScreen(navController = navController, Asistencia.registroId,
                viewModel_asistencia)
        }
        composable<Horario> { backStackEntry ->
            val Horario: Horario = backStackEntry.toRoute()
            HorarioScreen(navController = navController, Horario.registroId,
                viewModel_horario)
        }

        composable<AddHorario> { backStackEntry ->
            val AddHorario: AddHorario = backStackEntry.toRoute()
            AddHorarioScreen(navController = navController, AddHorario.registroId,
                viewModel_horario)
        }
        composable<EditHorario> { backStackEntry ->
            val EditHorario: EditHorario = backStackEntry.toRoute()
            EditHorarioScreen(navController = navController ,EditHorario.registroId,EditHorario.horarioId
               , viewModel_horario)
        }

        composable<Notas> { backStackEntry ->
            val Notas: Notas = backStackEntry.toRoute()
            NotasScreen(navController = navController, Notas.registroId,
                viewModel_notas)
        }

        composable<AddNotas> { backStackEntry ->
            val AddNotas: AddNotas = backStackEntry.toRoute()
            AddNotasScreen(navController = navController, AddNotas.registroId,
                viewModel_notas)
        }
        composable<EditNotas> { backStackEntry ->
            val EditNotas: EditNotas = backStackEntry.toRoute()
            EditNotasScreen(navController = navController ,EditNotas.registroId,EditNotas.notaId
                , viewModel_notas)
        }
        composable<Logros> { backStackEntry ->
            val Logros: Logros = backStackEntry.toRoute()
            LogrosScreen(navController = navController, Logros.registroId,
                viewModel_logros)
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
    }
}