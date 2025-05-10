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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fuudyapp.ui.viewmodel.RecipeViewModel

@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    recipeId: Int? = 1,
    viewModel: RecipeViewModel = viewModel()
) {
    // Colores
    val primaryGreen = Color(0xFF355E37)
    val backgroundColor = Color(0xFFF5F2EE)  // Color beige claro para el fondo

    // Lista de recetas
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

    // Seleccionar la receta según el ID
    val currentRecipeId = recipeId ?: 1
    val recipeIndex = (currentRecipeId - 1).coerceIn(0, recipes.size - 1)
    val recipe = recipes[recipeIndex]

    // Estado para favoritos
    var isFavorite by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // UI principal con diseño de tarjeta redondeada similar a la imagen de referencia
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Cabecera con imagen y botones
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Imagen principal
                Image(
                    painter = painterResource(id = recipe.imageResource),
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Botones superiores
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón de retroceso
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    // Botón de favorito (MODIFICADO)
                    IconButton(
                        onClick = {
                            // Convertir a String para Firebase y guardar en favoritos
                            viewModel.toggleFavorite(currentRecipeId.toString())
                            // Actualizar estado local
                            isFavorite = !isFavorite
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Red
                        )
                    }
                }
            }

            // Contenido de la receta en una tarjeta redondeada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Sección de "About"
                    Text(
                        text = "About",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = recipe.description,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Sección de "Ingredients"
                    Text(
                        text = "Ingredients",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Grid de ingredientes - AHORA DINÁMICO basado en la receta actual
                    val displayedIngredients = recipe.ingredients.take(4) // Tomar hasta 4 ingredientes para mostrar como íconos

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Mostramos los ingredientes de la receta actual
                        displayedIngredients.forEach { ingredient ->
                            IngredientIcon(
                                name = ingredient,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Si hay menos de 4 ingredientes, rellenamos con espacios vacíos
                        repeat(4 - displayedIngredients.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Lista completa de ingredientes en formato texto
                    recipe.ingredients.forEach { ingredient ->
                        IngredientItem(ingredient = ingredient)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sección de "Instructions"
                    Text(
                        text = "Instructions",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Instrucciones basadas en el tipo de receta
                    val instructions = when (recipe.id) {
                        1 -> listOf( // Banana Pancakes
                            "Mix mashed bananas, eggs, flour, cinnamon, and a pinch of salt.",
                            "Heat a non-stick pan over medium heat.",
                            "Pour about 1/4 cup of batter for each pancake.",
                            "Cook for 2-3 minutes until bubbles form, then flip and cook another minute."
                        )
                        2 -> listOf( // Tuna Poke
                            "Cube the fresh tuna into bite-sized pieces.",
                            "Mix soy sauce, sesame oil, and rice vinegar to make the dressing.",
                            "Combine tuna, dressing, and green onions. Let marinate for 10 minutes.",
                            "Serve over rice with additional toppings as desired."
                        )
                        3 -> listOf( // Avocado Toast
                            "Toast the bread until golden and crispy.",
                            "Mash the avocado and spread it on the toast.",
                            "Cook eggs as desired (poached recommended).",
                            "Top with eggs and garnish with cherry tomatoes."
                        )
                        else -> listOf( // Berry Yogurt Bowl
                            "Add Greek yogurt to a bowl.",
                            "Top with mixed berries.",
                            "Drizzle with honey.",
                            "Sprinkle granola on top and serve immediately."
                        )
                    }

                    instructions.forEachIndexed { index, instruction ->
                        InstructionStep(
                            number = index + 1,
                            instruction = instruction
                        )
                    }

                    // Espacio al final
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun IngredientIcon(
    name: String,
    modifier: Modifier = Modifier
) {
    // Asignar un emoji según el ingrediente para simulación de íconos
    val emoji = when {
        name.contains("Flour", ignoreCase = true) -> "🌾"
        name.contains("Banana", ignoreCase = true) -> "🍌"
        name.contains("Eggs", ignoreCase = true) -> "🥚"
        name.contains("Milk", ignoreCase = true) -> "🥛"
        name.contains("Tuna", ignoreCase = true) -> "🐟"
        name.contains("Soy", ignoreCase = true) -> "🍶"
        name.contains("Rice", ignoreCase = true) -> "🍚"
        name.contains("Oil", ignoreCase = true) -> "🫒"
        name.contains("Avocado", ignoreCase = true) -> "🥑"
        name.contains("Bread", ignoreCase = true) -> "🍞"
        name.contains("Tomato", ignoreCase = true) -> "🍅"
        name.contains("Yogurt", ignoreCase = true) -> "🥣"
        name.contains("Berries", ignoreCase = true) -> "🫐"
        name.contains("Honey", ignoreCase = true) -> "🍯"
        name.contains("Granola", ignoreCase = true) -> "🌾"
        name.contains("Cinnamon", ignoreCase = true) -> "🌰"
        name.contains("Garlic", ignoreCase = true) -> "🧄"
        name.contains("Onion", ignoreCase = true) -> "🧅"
        else -> "🍽️"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Círculo para el icono
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFF6F6F6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 30.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre del ingrediente
        Text(
            text = name,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun IngredientItem(
    ingredient: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Punto
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF355E37))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Nombre del ingrediente
        Text(
            text = ingredient,
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        )
    }
}

@Composable
private fun InstructionStep(
    number: Int,
    instruction: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Número del paso
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFF6F6F6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF355E37)
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Texto de la instrucción
        Text(
            text = instruction,
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray,
                lineHeight = 24.sp
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}