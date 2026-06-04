package com.ucsc.conectaucsc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ucsc.conectaucsc.ui.navigation.Login
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreen(viewModel: AuthViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido, ${viewModel.getUserName()}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "ConectaUCSC",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Mis Materias")
        }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Sesiones de Estudio")
        }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp)
        ) {
            Text("Mi Perfil")
        }

        OutlinedButton(
            onClick = {
                viewModel.logout()
                navController.navigate(Login) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}