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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().signOut()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authViewModel.authState) {
        when (authViewModel.authState) {
            AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            else -> {
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.wasa___avakado_),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.f_png),
                contentDescription = "Logo App",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "WELCOME!",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF355E37)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White.copy(alpha = 0.3f), // Fondo semitransparente
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFF355E37),
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black,  // Cambiado de textColor
                    unfocusedTextColor = Color.Black  // Añadido
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White.copy(alpha = 0.3f), // Fondo semitransparente
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFF355E37),
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black,   // Cambiado de textColor
                    unfocusedTextColor = Color.Black   // Añadido
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de login
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF355E37),
                    contentColor = Color.White
                )
            ) {
                if (authViewModel.authState == AuthViewModel.AuthState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Error mensaje
            if (authViewModel.authState is AuthViewModel.AuthState.Error) {
                Text(
                    text = (authViewModel.authState as AuthViewModel.AuthState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Enlace para registrarse
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Create an account", color = Color.White)
            }
        }
    }
}