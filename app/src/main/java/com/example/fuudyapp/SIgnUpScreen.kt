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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    LaunchedEffect(authViewModel.authState) {
        when (authViewModel.authState) {
            AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("signup") { inclusive = true }
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
                text = "CREATE ACCOUNT",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF355E37)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de nombre
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
                    focusedBorderColor = Color(0xFF355E37),
                    unfocusedBorderColor = Color(0xFF355E37).copy(alpha = 0.5f),
                    containerColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de email
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

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
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

            // Botón de registro
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.register(email, password, displayName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF355E37)),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp))
            ) {
                if (authViewModel.authState == AuthViewModel.AuthState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Up", fontSize = 16.sp)
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

            // Enlace para iniciar sesión
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Log in", color = Color.White)
            }
        }
    }
}