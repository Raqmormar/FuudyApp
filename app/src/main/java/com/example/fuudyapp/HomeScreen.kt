package com.example.fuudyapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fuudyapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    // Define los colores principales de la aplicación
    val primaryGreen = Color(0xFF355E37)

    // Define la fuente Epilogue
    val epilogueBold = FontFamily(
        Font(R.font.epilogue_bold)
    )

    // Estado para el campo de búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Estado para el desplazamiento
    val scrollState = rememberScrollState()

    // Obtiene la altura de la pantalla para calcular el espacio
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.bk_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Top bar con logo y botón de añadir receta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo que funciona como botón home
                Image(
                    painter = painterResource(id = R.drawable.fuudy_wb_1),
                    contentDescription = "Go to Home",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                )

                // Botón "+" para añadir receta
                FloatingActionButton(
                    onClick = { navController.navigate("add_recipe") },
                    containerColor = primaryGreen,
                    contentColor = Color.White,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Recipe",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "What do you want\nto cook today?",
                style = TextStyle(
                    fontFamily = epilogueBold,
                    color = primaryGreen,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Campo de búsqueda con texto "eat healthy..." superpuesto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Campo de búsqueda
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = primaryGreen
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    shape = RoundedCornerShape(50.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                )
            }

            // Espacio calculado dinámicamente para empujar las tarjetas hacia abajo
            val contentHeight = 330.dp
            val navBarHeight = 56.dp
            val spacerHeight = screenHeight - contentHeight - navBarHeight - 400.dp // Ajustado para las tarjetas más altas
            Spacer(modifier = Modifier.height(if (spacerHeight > 0.dp) spacerHeight else 100.dp))

            // Popular recipes section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular recipes",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "View all",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate("recipe_list")
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround, // Para centrar las tarjetas
                verticalAlignment = Alignment.CenterVertically
            ) {

                RecipeCard(
                    imageRes = R.drawable.pok_,
                    title = "",  // Sin texto
                    modifier = Modifier
                        .width(213.dp)  // Ancho exacto de 213dp
                        .height(359.dp) // Altura exacta de 359dp
                        .clickable {
                            navController.navigate("recipe_list")
                        }
                )
                RecipeCard(
                    imageRes = R.drawable.pancake,
                    title = "",  // Sin texto
                    modifier = Modifier
                        .width(213.dp)  // Ancho exacto de 213dp
                        .height(359.dp) // Altura exacta de 359dp
                        .clickable {
                            navController.navigate("recipe_list")
                        }
                )
            }

            // Espacio adicional mínimo para evitar que toque directamente la barra
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                navController = navController,
                primaryGreen = primaryGreen,
                currentRoute = "home"
            )
        }
    }
}

@Composable
private fun RecipeCard(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun BottomNavBar(
    navController: NavHostController,
    primaryGreen: Color,
    currentRoute: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = "Home",
                tint = if (currentRoute == "home") primaryGreen else Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
            )

            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = if (currentRoute == "recipe_list") primaryGreen else Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("recipe_list") }
            )

            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = if (currentRoute == "profile") primaryGreen else Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("profile") }
            )

            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorites",
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("favorites") }
            )
        }
    }
}