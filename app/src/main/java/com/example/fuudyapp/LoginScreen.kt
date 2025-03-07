import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fuudyapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    // Controlador del teclado
    val keyboardController = LocalSoftwareKeyboardController.current

    // Estado de los campos de texto
    var email by remember { mutableStateOf("") }  // Asegúrate de que esté un string vacío para el email
    var password by remember { mutableStateOf("") }  // Lo mismo para la contraseña

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.wasa___avakado_),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Para ajustar la imagen al tamaño de la pantalla
        )

        // Contenido encima de la imagen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "WELCOME!",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color (0xFF355E37)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de texto y botones (email, password, login button, etc.)
            TextField(
                onValueChange = {email = it},
                value = email,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)), // Hace el TextField redondeado
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White.copy(alpha = 0.3f), // Fondo con transparencia
                    focusedIndicatorColor = Color.Transparent, // Sin indicador de foco
                    unfocusedIndicatorColor = Color.Transparent // Sin indicador al perder foco
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(), // Para ocultar la contraseña
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50.dp)), // Hace el TextField redondeado
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White.copy(alpha = 0.3f), // Fondo con transparencia
                    focusedIndicatorColor = Color.Transparent, // Sin indicador de foco
                    unfocusedIndicatorColor = Color.Transparent // Sin indicador al perder foco
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF355E37))
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Create an account", color = Color.White)
            }
        }
    }
}
