package com.ucsc.conectaucsc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.ucsc.conectaucsc.ui.navigation.Login
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val authResult by viewModel.authResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var facultad by remember { mutableStateOf("") }

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
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = carrera,
            onValueChange = { carrera = it },
            label = { Text("Carrera") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = facultad,
            onValueChange = { facultad = it },
            label = { Text("Facultad") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            singleLine = true
        )

        Button(
            onClick = {
                viewModel.register(
                    name, email, password,
                    carrera.ifEmpty { null },
                    facultad.ifEmpty { null }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Registrarse")
            }
        }

        TextButton(onClick = { navController.navigate(Login) }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}