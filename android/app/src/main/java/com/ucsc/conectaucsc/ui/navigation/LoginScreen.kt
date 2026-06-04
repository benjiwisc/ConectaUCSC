package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ucsc.conectaucsc.ui.navigation.Home
import com.ucsc.conectaucsc.ui.navigation.Register
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val authResult by viewModel.authResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authResult) {
        authResult?.onSuccess {
            navController.navigate(Home) {
                popUpTo(0) { inclusive = true }
            }
        }?.onFailure {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ConectaUCSC",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            singleLine = true
        )

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Iniciar sesión")
            }
        }

        TextButton(onClick = { navController.navigate(Register) }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}