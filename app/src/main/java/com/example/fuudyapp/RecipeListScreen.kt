package com.example.fuudyapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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

// Modelo de datos mejorado para las recetas
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(navController: NavHostController) {
    // Colores de la aplicación
    val primaryGreen = Color(0xFF355E37)
    val lightGreen = Color(0xFFEDF1ED)
    val accentOrange = Color(0xFFF6913E)

    // Estados
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Datos de ejemplo mejorados
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

    // Filtrar recetas por categoría y búsqueda
    val filteredRecipes = recipes.filter { recipe ->
        (selectedCategory == "All" || recipe.category == selectedCategory) &&
                (searchQuery.isEmpty() || recipe.name.contains(searchQuery, ignoreCase = true) ||
                        recipe.description.contains(searchQuery, ignoreCase = true))
    }

    // UI principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Fondo con gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lightGreen.copy(alpha = 0.3f),
                            Color.White
                        )
                    )
                )
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Barra superior con título y botón de retroceso
            TopBar(
                title = "Discover Recipes",
                onBackClick = { navController.navigateUp() }
            )

            // Barra de búsqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                primaryColor = primaryGreen
            )

            // Categorías
            CategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                primaryGreen = primaryGreen,
                accentColor = accentOrange
            )

            // Lista de recetas
            if (filteredRecipes.isEmpty()) {
                EmptyRecipeList(query = searchQuery)
            } else {
                RecipeGrid(
                    recipes = filteredRecipes,
                    onClick = { recipe ->
                        // AQUÍ ESTÁ EL CAMBIO: Navegar a la pantalla de detalle con el ID
                        navController.navigate("recipe_detail/${recipe.id}")
                    },
                    primaryGreen = primaryGreen,
                    accentColor = accentOrange
                )
            }
        }

        // Barra de navegación
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(elevation = 8.dp)
                .background(Color.White)
        ) {
            BottomNavBar(
                navController = navController,
                primaryGreen = primaryGreen
            )
        }
    }
}

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
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Search healthy recipes...",
                style = TextStyle(color = Color.Gray, fontSize = 14.sp)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = primaryColor
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(50.dp)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = primaryColor
        ),
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp)
    )
}

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

@Composable
private fun CategoryPill(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    accentColor: Color
) {
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
            if (isSelected && category != "All") {
                Icon(
                    painter = painterResource(id = R.drawable.f_png), // Reemplazar con icono relevante
                    contentDescription = null,
                    tint = if (category == "Healthy") accentColor else Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }

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
                onClick = { onClick(recipes[index]) }, // Llama a la función onClick con la receta
                primaryColor = primaryGreen,
                accentColor = accentColor
            )
        }

        // Espacio extra para evitar que el contenido quede bajo la barra de navegación
        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun ModernRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    primaryColor: Color,
    accentColor: Color
) {
    var isFavorite by remember { mutableStateOf(recipe.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick), // Este onClick llamará a la función que navega a la pantalla de detalle
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        // Layout
        Column {
            // Imagen con overlay para título y botón de favorito
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imagen
                Image(
                    painter = painterResource(id = recipe.imageResource),
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                // Gradiente oscuro en la parte inferior para mejorar legibilidad
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
                                startY = 100f
                            )
                        )
                )

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

                // Botón de favorito
                IconButton(
                    onClick = { isFavorite = !isFavorite },
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

                // Tiempo de preparación
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

            // Información de la receta
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Etiqueta de categoría
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

                // Descripción
                Text(
                    text = recipe.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ingredientes principales
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    recipe.ingredients.take(3).forEachIndexed { index, ingredient ->
                        if (index > 0) {
                            Text(
                                text = " • ",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                        }

                        Text(
                            text = ingredient,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

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
            Image(
                painter = painterResource(id = R.drawable.f_png), // Reemplazar con imagen adecuada
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (query.isEmpty()) "No recipes found" else "No results for \"$query\"",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

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
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = "Home",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable { navController.navigate("home") }
        )

        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search",
            tint = primaryGreen, // Resaltado porque estamos en la página de búsqueda/recetas
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Ya estamos en la página de búsqueda */ }
        )

        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Profile",
            tint = Color.Gray,
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
