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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fuudyapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Background
        Image(
            painter = painterResource(id = R.drawable.wasa___avakado_),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize() // Para ajustar la imagen al tamaño de la pantalla
        )

        // Contenido encima de la imagen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource( id = R.drawable.f_png),
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
                color = Color (0xFF355E37)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de texto y botones (email, password, login button, etc.)
            TextField(
                value = "",
                onValueChange = {},
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
                value = "",
                onValueChange = {},
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
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("Login") }) {
                Text("Already have an account? Login", color = Color.White)
            }
        }
    }
}
