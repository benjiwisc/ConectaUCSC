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
import com.ucsc.conectaucsc.data.model.Facultad
import com.ucsc.conectaucsc.data.model.Carrera
import com.ucsc.conectaucsc.ui.navigation.Home
import com.ucsc.conectaucsc.ui.navigation.Login
import com.ucsc.conectaucsc.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val authResult by viewModel.authResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val facultades by viewModel.facultades.collectAsState()
    val carreras by viewModel.carreras.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedFacultad by remember { mutableStateOf<Facultad?>(null) }
    var selectedCarrera by remember { mutableStateOf<Carrera?>(null) }
    var facultadExpanded by remember { mutableStateOf(false) }
    var carreraExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadFacultades() }

    LaunchedEffect(authResult) {
        authResult?.onSuccess {
            navController.navigate(Home) { popUpTo(0) { inclusive = true } }
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

        // Dropdown Facultad
        ExposedDropdownMenuBox(
            expanded = facultadExpanded,
            onExpandedChange = { facultadExpanded = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedFacultad?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Facultad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facultadExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = facultadExpanded,
                onDismissRequest = { facultadExpanded = false }
            ) {
                facultades.forEach { facultad ->
                    DropdownMenuItem(
                        text = { Text(facultad.nombre) },
                        onClick = {
                            selectedFacultad = facultad
                            selectedCarrera = null
                            facultadExpanded = false
                            viewModel.loadCarreras(facultad.id)
                        }
                    )
                }
            }
        }

        // Dropdown Carrera
        ExposedDropdownMenuBox(
            expanded = carreraExpanded,
            onExpandedChange = { if (selectedFacultad != null) carreraExpanded = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            OutlinedTextField(
                value = selectedCarrera?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(if (selectedFacultad == null) "Selecciona primero una facultad" else "Carrera") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = carreraExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                enabled = selectedFacultad != null
            )
            ExposedDropdownMenu(
                expanded = carreraExpanded,
                onDismissRequest = { carreraExpanded = false }
            ) {
                carreras.forEach { carrera ->
                    DropdownMenuItem(
                        text = { Text(carrera.nombre) },
                        onClick = {
                            selectedCarrera = carrera
                            carreraExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.register(
                    name, email, password,
                    selectedFacultad?.id,
                    selectedCarrera?.id
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