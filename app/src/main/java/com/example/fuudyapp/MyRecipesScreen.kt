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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fuudyapp.models.Recipe
import com.example.fuudyapp.ui.viewmodel.RecipeViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla que muestra las recetas creadas por el usuario actual
 * Filtra las recetas por el usuario autenticado y permite gestión completa
 */
@Composable
fun MyRecipesScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel = viewModel()
) {
    // Paleta de colores consistente con la app
    val primaryGreen = Color(0xFF355E37)
    val accentOrange = Color(0xFFF6913E)
    val neutralBackground = Color(0xFFF8F7F5)
    val cardBackground = Color.White

    // Estados para gestionar la UI
    val allRecipes by viewModel.recipes.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    // Obtener usuario actual para filtrar recetas
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid ?: ""

    // Filtrar recetas del usuario actual (simulado por ahora con todas las recetas)
    // En una implementación real, las recetas tendrían un campo "createdBy"
    val myRecipes = allRecipes

    // Cargar recetas al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadRecipes()
        isLoading = false
    }

    // Dialog de confirmación para eliminar
    if (showDeleteDialog && recipeToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Receta") },
            text = { Text("¿Estás seguro de que quieres eliminar '${recipeToDelete?.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        recipeToDelete?.let { recipe ->
                            viewModel.deleteRecipeWithImage(
                                recipeId = recipe.id,
                                imageUrl = recipe.imageUrl
                            ) { success ->
                                if (success) {
                                    // Recargar lista después de eliminar
                                    viewModel.loadRecipes()
                                }
                            }
                        }
                        showDeleteDialog = false
                        recipeToDelete = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Estructura principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(neutralBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar con título y acción
            TopBar(
                title = "My Recipes",
                onBackClick = { navController.navigateUp() },
                onAddClick = { navController.navigate("add_recipe") },
                primaryGreen = primaryGreen
            )

            // Contenido principal
            if (isLoading) {
                // Estado de carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryGreen)
                }
            } else if (myRecipes.isEmpty()) {
                // Estado vacío
                EmptyMyRecipes(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    primaryGreen = primaryGreen,
                    onCreateRecipeClick = { navController.navigate("add_recipe") }
                )
            } else {
                // Lista de mis recetas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(myRecipes) { recipe ->
                        MyRecipeCard(
                            recipe = recipe,
                            onClick = {
                                navController.navigate("recipe_detail/${recipe.id}")
                            },
                            onEditClick = {
                                navController.navigate("edit_recipe/${recipe.id}")
                            },
                            onDeleteClick = {
                                recipeToDelete = recipe
                                showDeleteDialog = true
                            },
                            primaryGreen = primaryGreen,
                            accentColor = accentOrange
                        )
                    }

                    // Espacio para la navegación inferior
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
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
                currentRoute = "my_recipes"
            )
        }
    }
}

/**
 * Barra superior con título, botón de retroceso y botón de añadir
 */
@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    primaryGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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

            // Título
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

        // Botón de añadir receta
        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryGreen)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Recipe",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Estado vacío cuando el usuario no ha creado recetas
 */
@Composable
private fun EmptyMyRecipes(
    modifier: Modifier = Modifier,
    primaryGreen: Color,
    onCreateRecipeClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono grande de chef/cocinero
        Icon(
            imageVector = Icons.Outlined.AddCircle,
            contentDescription = null,
            tint = primaryGreen.copy(alpha = 0.3f),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mensaje principal
        Text(
            text = "No recipes created yet",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mensaje secundario
        Text(
            text = "Start creating your own delicious recipes and build your personal cookbook!",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para crear primera receta
        Button(
            onClick = onCreateRecipeClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryGreen
            ),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Your First Recipe",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * Tarjeta individual para cada receta del usuario con opciones de gestión
 */
@Composable
private fun MyRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    primaryGreen: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Sección superior: Imagen con overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Imagen de la receta usando AsyncImage para URLs de Firebase o fallback local
                if (recipe.imageUrl.isNotEmpty()) {
                    // Usar imagen de Firebase Storage
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.pancake),
                        error = painterResource(R.drawable.pancake)
                    )
                } else {
                    // Fallback a imagen local si no hay URL
                    val imageRes = when (recipe.id.toIntOrNull()) {
                        1 -> R.drawable.pancake
                        2 -> R.drawable.pok_
                        3 -> R.drawable.toste
                        4 -> R.drawable.yogurt
                        else -> R.drawable.pancake
                    }

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Gradiente para legibilidad
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 50f
                            )
                        )
                )

                // Título superpuesto
                Text(
                    text = recipe.name,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )

                // Botones de acción en la esquina superior derecha
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón editar
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Recipe",
                            tint = primaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Botón eliminar
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Recipe",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Sección inferior: Información de la receta
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Chip de categoría
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryGreen.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = recipe.category,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryGreen
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Descripción
                Text(
                    text = recipe.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Información adicional
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tiempo y dificultad
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⏱️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = recipe.prepTime,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📊", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = recipe.difficulty,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                        }
                    }

                    // Número de ingredientes
                    Text(
                        text = "${recipe.ingredients.size} ingredients",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

/**
 * Barra de navegación inferior
 */
@Composable
private fun BottomNavBar(
    navController: NavHostController,
    primaryGreen: Color,
    currentRoute: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = "Home",
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("home") }
            )

            // Search
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("recipe_list") }
            )

            // Profile
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("profile") }
            )

            // Favorites
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