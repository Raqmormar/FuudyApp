package com.example.fuudyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fuudyapp.R

data class Recipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val imageResource: Int
)

@Composable
fun RecipeListScreen(navController: NavHostController) {
    // Lista de recetas
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

    // Contenedor para la lista de recetas
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recipes.size) { index ->
            RecipeCard(recipe = recipes[index])
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Color de fondo suave
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen de la receta
            Image(
                painter = painterResource(id = recipe.imageResource),
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)), // Imagen redondeada
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre de la receta
            Text(
                text = recipe.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color (0xFF355E37)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción de la receta
            Text(
                text = recipe.description,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ingredientes
            Text(
                text = "Ingredientes:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            for (ingredient in recipe.ingredients) {
                Text(
                    text = "- $ingredient",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}
