package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Mantenemos tu data class Recipe original
data class Recipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val imageResource: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(navController: NavHostController) {
    // Define los colores principales de la aplicación
    val primaryGreen = Color(0xFF355E37)

    // Estado para el campo de búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Lista de recetas (usando tus datos originales)
    val recipes = listOf(
        Recipe(
            name = "Banana Pancake",
            description = "Fluffy, homemade banana pancakes.",
            ingredients = listOf("Flour", "Banana", "Cinnamon", "Eggs", "Milk"),
            imageResource = R.drawable.pancake
        ),
        Recipe(
            name = "Tuna Poke",
            description = "Hawaiian salad of fresh tuna steak cubes tossed with soy sauce, sesame oil, and green onions.",
            ingredients = listOf("Tuna", "Soy Sauce", "Sesame Oil", "Green Onions", "Toasted Sesame Seeds"),
            imageResource = R.drawable.pok_
        ),
        Recipe(
            name = "Avocado Toast",
            description = "Protein avocado toast for energetic breakfast.",
            ingredients = listOf("Eggs", "Gluten free bread", "Avocado"),
            imageResource = R.drawable.toste
        ),
        Recipe(
            name = "Fruits Bowl",
            description = "Bowl yogurt with berries.",
            ingredients = listOf("Kéfir or natural yogurt", "berries", "oat"),
            imageResource = R.drawable.yogurt
        )
    )

    // Categorías para los filtros
    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Snacks")
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Recipes",
                onBackClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                primaryGreen = primaryGreen
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.wasa___avakado_), // Reemplaza con tu imagen de fondo
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.15f // Ajusta la transparencia según necesites
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search recipes") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = primaryGreen
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = primaryGreen.copy(alpha = 0.3f),
                        focusedBorderColor = primaryGreen
                    )
                )

                // Categories
                Text(
                    text = "Categories",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Category chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        CategoryChip(
                            name = category,
                            isSelected = category == selectedCategory,
                            primaryGreen = primaryGreen,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                // Recipe list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recipes.size) { index ->
                        EnhancedRecipeCard(
                            recipe = recipes[index],
                            onClick = {
                                // Aquí puedes navegar a la pantalla de detalle de receta
                                // navController.navigate("recipe_detail/${index}")
                            }
                        )
                    }

                    // Agregar un espacio al final para mejorar la experiencia de desplazamiento
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF355E37)
            )
        }

        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF355E37)
            )
        )
    }
}

@Composable
private fun CategoryChip(
    name: String,
    isSelected: Boolean,
    primaryGreen: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (isSelected) primaryGreen else Color.White
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.White else Color.Black,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun EnhancedRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            // Imagen de la receta
            Image(
                painter = painterResource(id = recipe.imageResource),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // Información de la receta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF355E37)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recipe.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ingredients:",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF355E37)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Mostrar algunos ingredientes (los primeros 3)
                recipe.ingredients.take(3).forEach { ingredient ->
                    Text(
                        text = "• $ingredient",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    )
                }

                // Si hay más ingredientes, mostrar un "+ X more"
                if (recipe.ingredients.size > 3) {
                    Text(
                        text = "+ ${recipe.ingredients.size - 3} more",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFF355E37),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
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
                .clickable { /* Navegar a perfil */ }
        )

        Icon(
            painter = painterResource(id = R.drawable.f_png), // Reemplazar con un icono de marcador
            contentDescription = "Bookmarks",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Navegar a marcadores */ }
        )
    }
}