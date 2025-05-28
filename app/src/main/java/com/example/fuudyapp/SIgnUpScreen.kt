package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fuudyapp.ui.viewmodel.AuthViewModel

/**
 * Pantalla de registro de nuevos usuarios
 * Permite crear cuenta con nombre, email y contraseña
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    // ESTADOS LOCALES para los campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") } // Campo adicional vs LoginScreen

    // OBSERVADOR DEL ESTADO DE AUTENTICACIÓN
    // Navega automáticamente cuando el registro es exitoso
    LaunchedEffect(authViewModel.authState) {
        when (authViewModel.authState) {
            AuthViewModel.AuthState.Authenticated -> {
                // Registro exitoso: navegar a home y limpiar stack
                navController.navigate("home") {
                    popUpTo("signup") { inclusive = true }
                }
            }
            else -> {
                // Otros estados no requieren acción
            }
        }
    }

    // ESTRUCTURA PRINCIPAL con imagen de fondo
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo (misma que LoginScreen para consistencia)
        Image(
            painter = painterResource(id = R.drawable.wasa___avakado_),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // CONTENIDO PRINCIPAL centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOGO DE LA APLICACIÓN
            Image(
                painter = painterResource(id = R.drawable.f_png),
                contentDescription = "Logo App",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            // TÍTULO DE REGISTRO
            Text(
                text = "CREATE ACCOUNT",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF355E37) // Verde principal de Fuudy
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPO DE NOMBRE (único de SignUpScreen)
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF355E37), // Borde verde cuando está enfocado
                    unfocusedBorderColor = Color(0xFF355E37).copy(alpha = 0.5f), // Borde verde claro
                    containerColor = Color.White.copy(alpha = 0.3f) // Fondo semitransparente
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO DE EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF355E37),
                    unfocusedBorderColor = Color(0xFF355E37).copy(alpha = 0.5f),
                    containerColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO DE CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(), // Oculta texto con puntos
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF355E37),
                    unfocusedBorderColor = Color(0xFF355E37).copy(alpha = 0.5f),
                    containerColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN DE REGISTRO
            Button(
                onClick = {
                    // Validación básica: ambos campos obligatorios deben tener contenido
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        // Llamar función de registro en AuthViewModel
                        authViewModel.register(email, password, displayName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF355E37)),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp))
            ) {
                // CONTENIDO CONDICIONAL: Loading spinner o texto
                if (authViewModel.authState == AuthViewModel.AuthState.Loading) {
                    // Muestra spinner mientras procesa el registro
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    // Texto normal del botón
                    Text("Sign Up", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // MENSAJE DE ERROR (solo visible si hay error)
            if (authViewModel.authState is AuthViewModel.AuthState.Error) {
                Text(
                    text = (authViewModel.authState as AuthViewModel.AuthState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // ENLACE PARA INICIAR SESIÓN
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Log in", color = Color.White)
            }
        }
    }
}