package com.example.fuudyapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fuudyapp.ui.theme.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("recipe_list") { RecipeListScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("add_recipe") { AddEditRecipeScreen(navController) }
        composable("favorites") {
            FavoritesScreen(navController) }

        // Ruta para la pantalla de detalle de receta
        composable(
            route = "recipe_detail/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )

        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId")
            RecipeDetailScreen(navController = navController, recipeId = recipeId)
        }

        // Rutas para añadir/editar recetas
        composable("add_recipe") {
            AddEditRecipeScreen(navController = navController)
        }

        composable(
            route = "edit_recipe/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            AddEditRecipeScreen(navController = navController, recipeId = recipeId)
        }
    }
}