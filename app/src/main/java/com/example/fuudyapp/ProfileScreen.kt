package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ProfileScreen(navController: NavHostController) {
    // Define los colores principales de la aplicación
    val primaryGreen = Color(0xFF355E37)
    val backgroundColor = Color(0xFFF5F2EE)

    // Estado para el desplazamiento
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.bk_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Top bar con "Perfil"
            Text(
                text = "Profile",
                style = TextStyle(
                    color = primaryGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
            )

            // Sección de información del perfil
            ProfileHeader(
                name = "Raquel Mora",
                email = "raqmormor@gmail.com",
                profileImage = R.drawable.raq,
                primaryGreen = primaryGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Estadísticas del usuario
            UserStats(primaryGreen = primaryGreen)

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones del perfil
            ProfileOptions(
                primaryGreen = primaryGreen,
                onFavoritesClick = { /* Navegar a favoritos */ },
                onSettingsClick = { /* Navegar a configuración */ },
                onNotificationsClick = { /* Navegar a notificaciones */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de recetas guardadas
            Text(
                text = "Recetas guardadas",
                style = TextStyle(
                    color = primaryGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Tarjetas de recetas guardadas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SavedRecipeCard(
                    imageRes = R.drawable.pancake,
                    title = "Banana Pancakes",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Navegar a detalle de receta
                        }
                )

                SavedRecipeCard(
                    imageRes = R.drawable.yogurt,
                    title = "Fruits Bowl",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Navegar a detalle de receta
                        }
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // Espacio para la barra de navegación
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de perfil
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = profileImage),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Botón de editar perfil
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(primaryGreen)
                        .clickable { /* Editar perfil */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar perfil",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Información del usuario
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
            }
        }
    }
}

@Composable
private fun UserStats(primaryGreen: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            count = "23",
            label = "Recetas",
            primaryGreen = primaryGreen
        )

        StatItem(
            count = "12",
            label = "Guardadas",
            primaryGreen = primaryGreen
        )

        StatItem(
            count = "8",
            label = "Favoritas",
            primaryGreen = primaryGreen
        )
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String,
    primaryGreen: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = primaryGreen
            )
        )

        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray
            )
        )
    }
}

@Composable
private fun ProfileOptions(
    primaryGreen: Color,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileOptionItem(
                icon = Icons.Outlined.Favorite,
                title = "Mis favoritos",
                onClick = onFavoritesClick,
                primaryGreen = primaryGreen
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            ProfileOptionItem(
                icon = Icons.Outlined.Notifications,
                title = "Notificaciones",
                onClick = onNotificationsClick,
                primaryGreen = primaryGreen
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            ProfileOptionItem(
                icon = Icons.Outlined.Settings,
                title = "Configuración",
                onClick = onSettingsClick,
                primaryGreen = primaryGreen
            )
        }
    }
}

@Composable
private fun ProfileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
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
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = primaryGreen,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Ver más",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SavedRecipeCard(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay oscuro en la parte inferior para el texto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            // Título de la receta
            Text(
                text = title,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
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
                    .clickable { navController.navigate("home") }
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
                    .clickable { /* Ya estamos en el perfil */ }
            )

            // Icono de marcador personalizado
            Image(
                painter = painterResource(id = R.drawable.guardar_instagram),
                contentDescription = "Marcador",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { /* Navegar a marcadores */ },
                colorFilter = ColorFilter.tint(if (currentRoute == "bookmarks") primaryGreen else Color.Gray)
            )
        }
    }
}