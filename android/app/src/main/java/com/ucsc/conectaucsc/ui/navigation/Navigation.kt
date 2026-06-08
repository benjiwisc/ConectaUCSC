package com.ucsc.conectaucsc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucsc.conectaucsc.ui.screens.HomeScreen
import com.ucsc.conectaucsc.ui.screens.LoginScreen
import com.ucsc.conectaucsc.ui.screens.RegisterScreen
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel
import kotlinx.serialization.Serializable
import com.ucsc.conectaucsc.ui.screens.MateriasScreen

@Serializable object Login
@Serializable object Register
@Serializable object Home

@Serializable object Materias
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()

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
    }
}