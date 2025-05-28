package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla de inicio de sesión de la aplicación Fuudy
 * Permite autenticación con email y contraseña
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    // FORZAR LOGOUT AL INICIAR (para testing/desarrollo)
    // En producción esto debería eliminarse
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().signOut()
    }

    // ESTADOS LOCALES para los campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // OBSERVADOR DEL ESTADO DE AUTENTICACIÓN
    // Se ejecuta cada vez que cambia authState en el ViewModel
    LaunchedEffect(authViewModel.authState) {
        when (authViewModel.authState) {
            AuthViewModel.AuthState.Authenticated -> {
                // Si el login es exitoso, navega a home y limpia el stack
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
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
        // Imagen de fondo que cubre toda la pantalla
        Image(
            painter = painterResource(id = R.drawable.wasa___avakado_),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Recorta para mantener proporción
        )

        // CONTENIDO PRINCIPAL centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Centra verticalmente
            horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente
        ) {
            // LOGO DE LA APLICACIÓN
            Image(
                painter = painterResource(id = R.drawable.f_png),
                contentDescription = "Logo App",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit // Mantiene proporción sin recortar
            )

            // TÍTULO DE BIENVENIDA
            Text(
                text = "WELCOME!",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF355E37) // Verde principal de la app
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPO DE EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                singleLine = true, // Solo una línea de texto
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), // Botón "Siguiente" en teclado
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 80% del ancho disponible
                    .clip(RoundedCornerShape(50.dp)), // Bordes completamente redondeados
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White.copy(alpha = 0.3f), // Fondo blanco semitransparente
                    focusedBorderColor = Color.Transparent, // Sin borde cuando está enfocado
                    unfocusedBorderColor = Color.Transparent, // Sin borde cuando no está enfocado
                    cursorColor = Color(0xFF355E37), // Cursor verde
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black, // Texto negro cuando escribes
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO DE CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(), // Oculta el texto con puntos
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // Botón "Hecho" en teclado
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFF355E37),
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN DE LOGIN CON LÓGICA CONDICIONAL
            Button(
                onClick = {
                    // Solo procede si ambos campos tienen contenido
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f) // 50% del ancho disponible
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF355E37), // Verde principal
                    contentColor = Color.White
                )
            ) {
                // CONTENIDO DEL BOTÓN: Loading spinner o texto
                if (authViewModel.authState == AuthViewModel.AuthState.Loading) {
                    // Muestra spinner circular mientras carga
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    // Muestra texto normal
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // MENSAJE DE ERROR (solo se muestra si hay error)
            if (authViewModel.authState is AuthViewModel.AuthState.Error) {
                Text(
                    text = (authViewModel.authState as AuthViewModel.AuthState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // ENLACE PARA CREAR CUENTA
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Create an account", color = Color.White)
            }
        }
    }
}