package com.example.fuudyapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fuudyapp.ui.theme.HomeScreen

/**
 * Componente principal de navegación de la aplicación Fuudy
 * Define todas las rutas y pantallas disponibles en la app
 */

@Composable
fun AppNavigation() {
    // Crea y mantiene el controlador de navegación para toda la app
    val navController = rememberNavController()
    // NavHost: contenedor principal que maneja la navegación
    // startDestination: pantalla inicial cuando se abre la app
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