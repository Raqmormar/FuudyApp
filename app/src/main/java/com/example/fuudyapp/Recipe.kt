package com.example.fuudyapp.models

data class Recipe(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val imageUrl: String = "",  // URL de Firebase Storage
    val prepTime: String = "",
    val difficulty: String = "",
    val isFavorite: Boolean = false
)