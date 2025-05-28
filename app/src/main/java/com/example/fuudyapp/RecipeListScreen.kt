package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/**
 * Modelo de datos para las recetas (duplicado del otro archivo)
 * Define la estructura completa de información de cada receta
 */
data class Recipe(
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String = "Breakfast",
    val ingredients: List<String>,
    val prepTime: String = "20 min",
    val difficulty: String = "Easy",
    val isFavorite: Boolean = false,
    val imageResource: Int
)

/**
 * Pantalla principal de lista de recetas con búsqueda y filtros
 * Funcionalidad completa: búsqueda en tiempo real, categorías, navegación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(navController: NavHostController) {
    // PALETA DE COLORES de la aplicación Fuudy
    val primaryGreen = Color(0xFF355E37)
    val lightGreen = Color(0xFFEDF1ED)
    val accentOrange = Color(0xFFF6913E)

    // ESTADOS REACTIVOS para filtros y búsqueda
    var searchQuery by remember { mutableStateOf("") } // Texto de búsqueda del usuario
    var selectedCategory by remember { mutableStateOf("All") } // Categoría seleccionada

    // DATOS MOCK - En aplicación real vendrían de Firebase via ViewModel
    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Desserts", "Healthy")
    val recipes = remember {
        listOf(
            Recipe(
                id = 1,
                name = "Banana Pancakes",
                description = "Fluffy, homemade banana pancakes with maple syrup and fresh berries.",
                category = "Breakfast",
                ingredients = listOf("Flour", "Banana", "Cinnamon", "Eggs", "Milk"),
                prepTime = "15 min",
                difficulty = "Easy",
                imageResource = R.drawable.pancake
            ),
            Recipe(
                id = 2,
                name = "Tuna Poke Bowl",
                description = "Fresh tuna cubes with avocado, rice, edamame and ponzu sauce.",
                category = "Lunch",
                ingredients = listOf("Tuna", "Soy Sauce", "Sesame Oil", "Green Onions", "Rice"),
                prepTime = "25 min",
                difficulty = "Medium",
                imageResource = R.drawable.pok_
            ),
            Recipe(
                id = 3,
                name = "Avocado Toast",
                description = "Protein-packed breakfast with avocado, poached eggs and whole grain bread.",
                category = "Breakfast",
                ingredients = listOf("Eggs", "Gluten-free bread", "Avocado", "Cherry tomatoes"),
                prepTime = "10 min",
                difficulty = "Easy",
                imageResource = R.drawable.toste
            ),
            Recipe(
                id = 4,
                name = "Berry Yogurt Bowl",
                description = "Greek yogurt with fresh berries, honey and homemade granola.",
                category = "Breakfast",
                ingredients = listOf("Greek yogurt", "Mixed berries", "Honey", "Granola"),
                prepTime = "5 min",
                difficulty = "Easy",
                imageResource = R.drawable.yogurt
            )
        )
    }

    // LÓGICA DE FILTRADO: combina filtro de categoría y búsqueda de texto
    val filteredRecipes = recipes.filter { recipe ->
        (selectedCategory == "All" || recipe.category == selectedCategory) &&
                (searchQuery.isEmpty() || recipe.name.contains(searchQuery, ignoreCase = true) ||
                        recipe.description.contains(searchQuery, ignoreCase = true))
    }

    // ESTRUCTURA PRINCIPAL DE LA PANTALLA
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // FONDO CON GRADIENTE SUTIL
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lightGreen.copy(alpha = 0.3f), // Verde claro en la parte superior
                            Color.White // Blanco en la parte inferior
                        )
                    )
                )
        )

        // CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Barra superior con título y navegación
            TopBar(
                title = "Discover Recipes",
                onBackClick = { navController.navigateUp() }
            )

            // Campo de búsqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }, // Actualiza búsqueda en tiempo real
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                primaryColor = primaryGreen
            )

            // Selector horizontal de categorías
            CategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }, // Actualiza filtro de categoría
                primaryGreen = primaryGreen,
                accentColor = accentOrange
            )

            // CONTENIDO CONDICIONAL: Lista o mensaje de estado vacío
            if (filteredRecipes.isEmpty()) {
                EmptyRecipeList(query = searchQuery)
            } else {
                RecipeGrid(
                    recipes = filteredRecipes,
                    onClick = { recipe ->
                        // NAVEGACIÓN: Ir a pantalla de detalle con el ID de la receta
                        navController.navigate("recipe_detail/${recipe.id}")
                    },
                    primaryGreen = primaryGreen,
                    accentColor = accentOrange
                )
            }
        }

        // BARRA DE NAVEGACIÓN INFERIOR FLOTANTE
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(elevation = 8.dp) // Sombra para efecto flotante
                .background(Color.White)
        ) {
            BottomNavBar(
                navController = navController,
                primaryGreen = primaryGreen
            )
        }
    }
}

/**
 * Componente de barra superior con título y botón de retroceso
 */
@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de retroceso con estilo circular
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF355E37),
                modifier = Modifier.size(20.dp)
            )
        }

        // Título de la pantalla
        Text(
            text = title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF355E37)
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

/**
 * Componente de búsqueda con icono de lupa y placeholder
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color
) {
    TextField(
        value = query,
        onValueChange = onQueryChange, // Actualización en tiempo real mientras se escribe
        placeholder = {
            Text(
                "Search healthy recipes...",
                style = TextStyle(color = Color.Gray, fontSize = 14.sp)
            )
        },
        leadingIcon = {
            // Icono de búsqueda al inicio del campo
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = primaryColor
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(50.dp)), // Bordes completamente redondeados
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent, // Sin línea inferior
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = primaryColor
        ),
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp)
    )
}

/**
 * Componente selector de categorías en formato horizontal deslizante
 */
@Composable
private fun CategorySelector(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    primaryGreen: Color,
    accentColor: Color
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryPill(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                primaryColor = primaryGreen,
                accentColor = accentColor
            )
        }
    }
}

/**
 * Componente individual de categoría en formato pill/chip
 * Cambia estilo según si está seleccionada o no
 */
@Composable
private fun CategoryPill(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    accentColor: Color
) {
    // ESTILOS CONDICIONALES según selección
    val backgroundColor = if (isSelected) primaryColor else Color.White
    val textColor = if (isSelected) Color.White else Color.DarkGray
    val shadowElevation = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .shadow(shadowElevation, RoundedCornerShape(50.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icono opcional para categorías seleccionadas (excepto "All")
            if (isSelected && category != "All") {
                Icon(
                    painter = painterResource(id = R.drawable.f_png), // Placeholder - reemplazar con icono específico
                    contentDescription = null,
                    tint = if (category == "Healthy") accentColor else Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }

            // Texto de la categoría
            Text(
                text = category,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            )
        }
    }
}

/**
 * Componente que renderiza la lista/grid de recetas de forma eficiente
 */
@Composable
private fun RecipeGrid(
    recipes: List<Recipe>,
    onClick: (Recipe) -> Unit,
    primaryGreen: Color,
    accentColor: Color
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recipes.size) { index ->
            ModernRecipeCard(
                recipe = recipes[index],
                onClick = { onClick(recipes[index]) }, // Pasa la receta completa al callback
                primaryColor = primaryGreen,
                accentColor = accentColor
            )
        }

        // Espacio adicional para evitar solapamiento con barra de navegación
        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

/**
 * Tarjeta moderna de receta con diseño tipo Instagram/Pinterest
 * Incluye imagen con overlay, información y botón de favorito
 */
@Composable
private fun ModernRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    primaryColor: Color,
    accentColor: Color
) {
    // Estado local para el botón de favorito (en app real vendría del ViewModel)
    var isFavorite by remember { mutableStateOf(recipe.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick), // Toda la tarjeta es clickeable
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            // SECCIÓN SUPERIOR: Imagen con overlays superpuestos
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imagen principal de la receta
                Image(
                    painter = painterResource(id = recipe.imageResource),
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                // Gradiente oscuro para mejorar legibilidad del texto superpuesto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 100f // Gradiente solo en la mitad inferior
                            )
                        )
                )

                // Título de la receta superpuesto en la imagen
                Text(
                    text = recipe.name,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )

                // Botón de favorito (esquina superior derecha)
                IconButton(
                    onClick = { isFavorite = !isFavorite }, // Toggle local del estado
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) accentColor else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Chip de tiempo de preparación (esquina inferior derecha)
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️", // Emoji de reloj
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = recipe.prepTime,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // SECCIÓN INFERIOR: Información detallada de la receta
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Etiqueta de categoría con fondo coloreado
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = recipe.category,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Descripción de la receta (limitada a 2 líneas)
                Text(
                    text = recipe.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis // Añade "..." si es muy largo
                )

                Spacer(modifier = Modifier.height(12.dp))

                // LISTA DE INGREDIENTES PRINCIPALES (solo los primeros 3)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    recipe.ingredients.take(3).forEachIndexed { index, ingredient ->
                        // Separador entre ingredientes (excepto el primero)
                        if (index > 0) {
                            Text(
                                text = " • ",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                        }

                        // Nombre del ingrediente
                        Text(
                            text = ingredient,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Indicador de ingredientes adicionales si hay más de 3
                    if (recipe.ingredients.size > 3) {
                        Text(
                            text = " +${recipe.ingredients.size - 3} more",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente mostrado cuando no hay recetas que coincidan con los filtros
 */
@Composable
private fun EmptyRecipeList(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen/logo como indicador de estado vacío
            Image(
                painter = painterResource(id = R.drawable.f_png), // Logo de Fuudy
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(Color.LightGray) // Tinte gris para indicar vacío
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje principal condicional según si hay búsqueda activa
            Text(
                text = if (query.isEmpty()) "No recipes found" else "No results for \"$query\"",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje secundario con sugerencias
            Text(
                text = if (query.isEmpty())
                    "Try selecting a different category"
                else
                    "Try different keywords or browse by category",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

/**
 * Barra de navegación inferior con 4 pestañas principales
 */
@Composable
private fun BottomNavBar(
    navController: NavHostController,
    primaryGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pestaña Home
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = "Home",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable { navController.navigate("home") }
        )

        // Pestaña Search (activa en esta pantalla)
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search",
            tint = primaryGreen, // Color resaltado porque estamos en la pantalla de búsqueda
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Ya estamos en la página de búsqueda */ }
        )

        // Pestaña Profile
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Profile",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable { navController.navigate("profile") }
        )

        // Pestaña Favorites
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