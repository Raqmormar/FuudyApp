package com.example.fuudyapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    // Define los colores principales de la aplicación
    val primaryGreen = Color(0xFF355E37)
    val lightGreen = Color(0xFFEDF1ED)
    val neutralBackground = Color(0xFFF8F7F5) // Color neutral para el fondo
    val cardBackground = Color.White

    // Variables para efectos de animación
    var showStats by remember { mutableStateOf(false) }

    // Estado para el desplazamiento
    val scrollState = rememberScrollState()

    // Activar animación después de la carga inicial
    LaunchedEffect(key1 = true) {
        showStats = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(neutralBackground)
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Top bar con "Profile"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Profile",
                    style = TextStyle(
                        color = primaryGreen,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Botón de configuración
                IconButton(
                    onClick = { /* Navegación a configuración */ },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(primaryGreen.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = primaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de información del perfil
            ProfileHeader(
                name = "Raquel Mora",
                email = "raqmormor@gmail.com",
                profileImage = R.drawable.raq,
                primaryGreen = primaryGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Estadísticas del usuario con animación
            AnimatedVisibility(
                visible = showStats,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                        expandVertically(animationSpec = tween(durationMillis = 500))
            ) {
                UserActivityStats(primaryGreen = primaryGreen)
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileOptions(
                primaryGreen = primaryGreen,
                onMyRecipesClick = { /* Navegar a mis recetas */ },
                onFavoritesClick = { navController.navigate("favorites") },
                onDietaryPreferencesClick = { /* Navegar a preferencias dietéticas */ }
            )

            // Aseguramos espacio suficiente para la navegación inferior
            Spacer(modifier = Modifier.height(80.dp))
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
                currentRoute = "profile"
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    profileImage: Int,
    primaryGreen: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil con borde
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Círculo exterior decorativo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(primaryGreen.copy(alpha = 0.1f))
                )

                // Imagen de perfil
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Image(
                        painter = painterResource(id = profileImage),
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Botón de editar perfil
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(primaryGreen)
                            .clickable { /* Editar perfil */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información del usuario
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Indicador de nivel de usuario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(primaryGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Level",
                        tint = primaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                }
            }
        }
    }
}

@Composable
private fun UserActivityStats(primaryGreen: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                count = "23",
                label = "My Recipes",
                icon = Icons.Outlined.Star,
                primaryGreen = primaryGreen
            )

            // Separador vertical
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            StatItem(
                count = "12",
                label = "Favorites",
                icon = Icons.Filled.Favorite,
                primaryGreen = primaryGreen
            )

            // Separador vertical
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            StatItem(
                count = "4",
                label = "Preferences",
                icon = Icons.Outlined.AddCircle,
                primaryGreen = primaryGreen
            )
        }
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryGreen: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primaryGreen,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = count,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        )

        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}

@Composable
private fun ProfileOptions(
    primaryGreen: Color,
    onMyRecipesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onDietaryPreferencesClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileOptionItem(
                icon = Icons.Outlined.Star,
                title = "My Recipes",
                subtitle = "Manage your created recipes",
                onClick = onMyRecipesClick,
                primaryGreen = primaryGreen
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            ProfileOptionItem(
                icon = Icons.Filled.Favorite,
                title = "My Favorites",
                subtitle = "View all your favorite recipes",
                onClick = onFavoritesClick,
                primaryGreen = primaryGreen
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            ProfileOptionItem(
                icon = Icons.Outlined.AddCircle,
                title = "Dietary Preferences",
                subtitle = "Set your dietary needs and restrictions",
                onClick = onDietaryPreferencesClick,
                primaryGreen = primaryGreen
            )
        }
    }
}

@Composable
private fun ProfileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    primaryGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono con fondo circular
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryGreen.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = primaryGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        // Textos
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )

            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
        }

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Ver más",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun BottomNavBar(
    navController: NavHostController,
    primaryGreen: Color,
    currentRoute: String
) {
    // Barra inferior elevada con efecto de sombra
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de Home
            NavBarItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = currentRoute == "home",
                primaryColor = primaryGreen,
                onClick = { navController.navigate("home") }
            )

            // Botón de Search
            NavBarItem(
                icon = Icons.Outlined.Search,
                label = "Search",
                isSelected = currentRoute == "recipe_list",
                primaryColor = primaryGreen,
                onClick = { navController.navigate("recipe_list") }
            )

            // Botón de Profile
            NavBarItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                isSelected = currentRoute == "profile",
                primaryColor = primaryGreen,
                onClick = { /* Ya estamos en profile */ }
            )

            // Botón de Favorites
            NavBarItem(
                icon = Icons.Outlined.FavoriteBorder,
                label = "Favorites",
                isSelected = currentRoute == "favorites",
                primaryColor = primaryGreen,
                onClick = { navController.navigate("favorites") }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) primaryColor else Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) primaryColor else Color.Gray
            )
        )
    }
}