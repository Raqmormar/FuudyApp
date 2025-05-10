package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fuudyapp.models.Recipe
import com.example.fuudyapp.ui.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel = viewModel()
) {
    // Color principal
    val primaryGreen = Color(0xFF355E37)
    val accentOrange = Color(0xFFF6913E)
    val neutralBackground = Color(0xFFF8F7F5) // Color neutral para el fondo

    // Estado para las recetas favoritas
    val favoriteRecipes = remember { mutableStateListOf<Recipe>() }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Efecto para cargar las recetas favoritas desde Firebase
    LaunchedEffect(Unit) {
        // Creamos el repositorio
        val favoritesRepository = FavoritesRepository()

        // Obtenemos los IDs de las recetas favoritas del usuario
        favoritesRepository.getFavoriteRecipes(
            onSuccess = { favoriteIds ->
                // Convertimos los IDs en recetas completas
                // Normalmente aquí harías una llamada a otro repositorio para obtener los detalles
                // de cada receta por su ID, pero por ahora usaremos datos de ejemplo

                val recipeMap = mapOf(
                    "1" to Recipe(
                        id = "1",
                        name = "Banana Pancakes",
                        description = "Delicious pancakes with fresh bananas",
                        category = "Breakfast",
                        ingredients = listOf("Flour", "Milk", "Eggs", "Bananas"),
                        instructions = listOf("Mix all ingredients", "Cook on pan"),
                        imageUrl = "",
                        prepTime = "20 min",
                        difficulty = "Easy"
                    ),
                    "3" to Recipe(
                        id = "3",
                        name = "Avocado Toast",
                        description = "Simple and nutritious avocado toast",
                        category = "Breakfast",
                        ingredients = listOf("Bread", "Avocado", "Lime", "Salt"),
                        instructions = listOf("Toast bread", "Mash avocado", "Spread on toast"),
                        imageUrl = "",
                        prepTime = "5 min",
                        difficulty = "Easy"
                    ),
                    "4" to Recipe(
                        id = "4",
                        name = "Greek Yogurt Bowl",
                        description = "Creamy yogurt with fruits and honey",
                        category = "Breakfast",
                        ingredients = listOf("Greek yogurt", "Berries", "Honey", "Granola"),
                        instructions = listOf("Add yogurt to bowl", "Top with fruits and honey"),
                        imageUrl = "",
                        prepTime = "5 min",
                        difficulty = "Easy"
                    )
                )

                // Filtrar solo las recetas que existen en nuestro mapa
                val recipes = favoriteIds.mapNotNull { id -> recipeMap[id] }

                favoriteRecipes.clear()
                favoriteRecipes.addAll(recipes)
                isLoading = false
            },
            onError = { exception ->
                isLoading = false
                hasError = true
                errorMessage = exception.message ?: "Error al cargar favoritos"
                println("Error al cargar favoritos: ${exception.message}")
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(neutralBackground)
    ) {
        // Contenido principal
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra superior
            TopBar(
                title = "My Favorite Recipes",
                onBackClick = { navController.navigateUp() },
                primaryGreen = primaryGreen
            )

            // Lista de recetas favoritas
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryGreen)
                }
            } else if (hasError) {
                // Mensaje de error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ups! Algo salió mal",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGreen
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = errorMessage,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                hasError = false
                                // Reintentar cargar los favoritos
                                val favoritesRepository = FavoritesRepository()
                                favoritesRepository.getFavoriteRecipes(
                                    onSuccess = { favoriteIds ->
                                        // Mismo código que arriba
                                        val recipeMap = mapOf(
                                            "1" to Recipe(
                                                id = "1",
                                                name = "Banana Pancakes",
                                                description = "Delicious pancakes with fresh bananas",
                                                category = "Breakfast",
                                                ingredients = listOf("Flour", "Milk", "Eggs", "Bananas"),
                                                instructions = listOf("Mix all ingredients", "Cook on pan"),
                                                imageUrl = "",
                                                prepTime = "20 min",
                                                difficulty = "Easy"
                                            ),
                                            "3" to Recipe(
                                                id = "3",
                                                name = "Avocado Toast",
                                                description = "Simple and nutritious avocado toast",
                                                category = "Breakfast",
                                                ingredients = listOf("Bread", "Avocado", "Lime", "Salt"),
                                                instructions = listOf("Toast bread", "Mash avocado", "Spread on toast"),
                                                imageUrl = "",
                                                prepTime = "5 min",
                                                difficulty = "Easy"
                                            ),
                                            "4" to Recipe(
                                                id = "4",
                                                name = "Greek Yogurt Bowl",
                                                description = "Creamy yogurt with fruits and honey",
                                                category = "Breakfast",
                                                ingredients = listOf("Greek yogurt", "Berries", "Honey", "Granola"),
                                                instructions = listOf("Add yogurt to bowl", "Top with fruits and honey"),
                                                imageUrl = "",
                                                prepTime = "5 min",
                                                difficulty = "Easy"
                                            )
                                        )

                                        val recipes = favoriteIds.mapNotNull { id -> recipeMap[id] }

                                        favoriteRecipes.clear()
                                        favoriteRecipes.addAll(recipes)
                                        isLoading = false
                                    },
                                    onError = { exception ->
                                        isLoading = false
                                        hasError = true
                                        errorMessage = exception.message ?: "Error al cargar favoritos"
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryGreen
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            } else if (favoriteRecipes.isEmpty()) {
                // Mensaje cuando no hay favoritos
                EmptyFavorites(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    primaryGreen = primaryGreen,
                    onExploreClick = { navController.navigate("recipe_list") }
                )
            } else {
                // Lista de recetas favoritas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(favoriteRecipes) { recipe ->
                        FavoriteRecipeCard(
                            recipe = recipe,
                            onClick = {
                                navController.navigate("recipe_detail/${recipe.id}")
                            },
                            onFavoriteClick = {
                                // Aquí se llama al repositorio para eliminar de favoritos
                                val favoritesRepository = FavoritesRepository()
                                favoritesRepository.removeFromFavorites(
                                    recipeId = recipe.id,
                                    onSuccess = {
                                        // Eliminamos de la UI solo si Firebase tiene éxito
                                        favoriteRecipes.remove(recipe)
                                    },
                                    onError = { exception ->
                                        // Aquí podrías mostrar un mensaje de error
                                        println("Error al eliminar favorito: ${exception.message}")
                                    }
                                )
                            },
                            primaryGreen = primaryGreen,
                            accentColor = accentOrange
                        )
                    }

                    // Espacio para la barra de navegación
                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }

        // Barra de navegación inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                navController = navController,
                primaryGreen = primaryGreen,
                currentRoute = "favorites"
            )
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit,
    primaryGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de retroceso
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = primaryGreen
            )
        }

        // Título de la pantalla
        Text(
            text = title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun EmptyFavorites(
    modifier: Modifier = Modifier,
    primaryGreen: Color,
    onExploreClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de corazón vacío
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = primaryGreen.copy(alpha = 0.3f),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mensaje
        Text(
            text = "No favorite recipes yet",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start exploring recipes and save your favorites!",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para explorar recetas
        Button(
            onClick = onExploreClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryGreen
            ),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Explore Recipes",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun FavoriteRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    primaryGreen: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de la receta
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            ) {
                // Si hay una URL de imagen, usaríamos Coil para cargarla
                // Por ahora, usamos una imagen de recurso
                val imageRes = when (recipe.id) {
                    "1" -> R.drawable.pancake
                    "2" -> R.drawable.pok_
                    "3" -> R.drawable.toste
                    "4" -> R.drawable.yogurt
                    else -> R.drawable.pancake
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Información de la receta
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Nombre de la receta
                    Text(
                        text = recipe.name,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón de favorito
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Remove from Favorites",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Categoría
                Text(
                    text = recipe.category,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = primaryGreen
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tiempo de preparación
                Text(
                    text = "⏱️ ${recipe.prepTime}",
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
                imageVector = if (currentRoute == "favorites") Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorites",
                tint = if (currentRoute == "favorites") primaryGreen else Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        if (currentRoute != "favorites") {
                            navController.navigate("favorites")
                        }
                    }
            )
        }
    }
}