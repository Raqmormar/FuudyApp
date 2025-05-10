package com.example.fuudyapp.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuudyapp.FavoritesRepository
import com.example.fuudyapp.data.repository.RecipeRepository
import com.example.fuudyapp.data.repository.StorageRepository
import com.example.fuudyapp.data.repository.UserRepository
import com.example.fuudyapp.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository()
    private val userRepository = UserRepository()
    private val storageRepository = StorageRepository()
    private val favoritesRepository = FavoritesRepository() // Nuevo repositorio para favoritos

    // Estado para la lista de recetas
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    // Estado para los favoritos
    private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes.asStateFlow()

    // Estado para los IDs de favoritos (nuevo)
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // Estado de carga
    var isLoading by mutableStateOf(false)
        private set

    // Estado de error
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Estados para la carga de imágenes
    var isUploading by mutableStateOf(false)
        private set

    var uploadProgress by mutableStateOf(0f)
        private set

    init {
        loadRecipes()
        loadFavoriteRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val recipeList = recipeRepository.getAllRecipes()
                _recipes.value = recipeList
            } catch (e: Exception) {
                errorMessage = "Error loading recipes: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadFavoriteRecipes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Usar el nuevo FavoritesRepository
                favoritesRepository.getFavoriteRecipes(
                    onSuccess = { favoriteIds ->
                        _favoriteIds.value = favoriteIds.toSet()

                        if (favoriteIds.isNotEmpty()) {
                            // Cargar las recetas favoritas usando los IDs
                            viewModelScope.launch {
                                try {
                                    val favorites = recipeRepository.getFavoriteRecipes(favoriteIds)
                                    _favoriteRecipes.value = favorites
                                } catch (e: Exception) {
                                    errorMessage = "Error loading favorite recipes: ${e.message}"
                                    _favoriteRecipes.value = emptyList()
                                }
                            }
                        } else {
                            _favoriteRecipes.value = emptyList()
                        }

                        isLoading = false
                    },
                    onError = { e ->
                        errorMessage = "Error loading favorite recipes: ${e.message}"
                        _favoriteRecipes.value = emptyList()
                        _favoriteIds.value = emptySet()
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                errorMessage = "Error loading favorite recipes: ${e.message}"
                isLoading = false
            }
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            try {
                // Verificar si ya está en favoritos
                val isFavorite = _favoriteIds.value.contains(recipeId)

                if (isFavorite) {
                    // Eliminar de favoritos
                    favoritesRepository.removeFromFavorites(
                        recipeId,
                        onSuccess = {
                            // Actualizar inmediatamente el estado local
                            _favoriteIds.value = _favoriteIds.value - recipeId
                            _favoriteRecipes.value = _favoriteRecipes.value.filter { it.id != recipeId }
                        },
                        onError = { e ->
                            errorMessage = "Error removing from favorites: ${e.message}"
                        }
                    )
                } else {
                    // Añadir a favoritos
                    favoritesRepository.addToFavorites(
                        recipeId,
                        onSuccess = {
                            // Actualizar inmediatamente el estado local
                            _favoriteIds.value = _favoriteIds.value + recipeId

                            // Si tenemos la receta en la lista principal, añadirla a favoritos
                            val recipe = _recipes.value.find { it.id == recipeId }
                            if (recipe != null && !_favoriteRecipes.value.any { it.id == recipeId }) {
                                _favoriteRecipes.value = _favoriteRecipes.value + recipe
                            }
                        },
                        onError = { e ->
                            errorMessage = "Error adding to favorites: ${e.message}"
                        }
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Error updating favorites: ${e.message}"
            }
        }
    }

    // El resto de tus funciones sin cambios...

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                if (query.isBlank()) {
                    loadRecipes()
                } else {
                    val searchResults = recipeRepository.searchRecipes(query)
                    _recipes.value = searchResults
                }
            } catch (e: Exception) {
                errorMessage = "Error searching recipes: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getRecipeById(recipeId: String, onResult: (Recipe?) -> Unit) {
        viewModelScope.launch {
            try {
                val recipe = recipeRepository.getRecipeById(recipeId)
                onResult(recipe)
            } catch (e: Exception) {
                errorMessage = "Error getting recipe: ${e.message}"
                onResult(null)
            }
        }
    }

    // Funciones para gestión de imágenes sin cambios...

    fun addRecipeWithImage(recipe: Recipe, imageUri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isUploading = true
            uploadProgress = 0f
            errorMessage = null

            try {
                // Primero crear la receta para obtener su ID
                val tempRecipe = recipe.copy(imageUrl = "")
                val recipeId = recipeRepository.addRecipe(tempRecipe)

                if (recipeId != null) {
                    // Si hay una imagen, la subimos
                    var imageUrl = ""
                    if (imageUri != null) {
                        uploadProgress = 0.3f
                        imageUrl = storageRepository.uploadRecipeImage(imageUri, recipeId) ?: ""
                        uploadProgress = 0.7f
                    }

                    // Actualizamos la receta con la URL de la imagen
                    val updatedRecipe = recipe.copy(id = recipeId, imageUrl = imageUrl)
                    val success = recipeRepository.updateRecipe(updatedRecipe)
                    uploadProgress = 1f

                    // Refrescar la lista de recetas
                    loadRecipes()

                    onComplete(success)
                } else {
                    errorMessage = "No se pudo crear la receta"
                    onComplete(false)
                }
            } catch (e: Exception) {
                errorMessage = "Error al añadir receta: ${e.message}"
                onComplete(false)
            } finally {
                isUploading = false
                uploadProgress = 0f
            }
        }
    }

    fun updateRecipeWithImage(recipe: Recipe, imageUri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isUploading = true
            uploadProgress = 0f
            errorMessage = null

            try {
                var imageUrl = recipe.imageUrl

                // Si hay una nueva imagen seleccionada
                if (imageUri != null) {
                    uploadProgress = 0.2f

                    // Si ya había una imagen anterior, la eliminamos
                    if (imageUrl.isNotEmpty()) {
                        storageRepository.deleteRecipeImage(imageUrl)
                    }

                    // Subimos la nueva imagen
                    uploadProgress = 0.5f
                    imageUrl = storageRepository.uploadRecipeImage(imageUri, recipe.id) ?: ""
                    uploadProgress = 0.8f
                }

                // Actualizamos la receta con la nueva URL
                val updatedRecipe = recipe.copy(imageUrl = imageUrl)
                val success = recipeRepository.updateRecipe(updatedRecipe)
                uploadProgress = 1f

                // Refrescar datos
                loadRecipes()
                loadFavoriteRecipes()

                onComplete(success)
            } catch (e: Exception) {
                errorMessage = "Error al actualizar receta: ${e.message}"
                onComplete(false)
            } finally {
                isUploading = false
                uploadProgress = 0f
            }
        }
    }

    fun deleteRecipeWithImage(recipeId: String, imageUrl: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Primero eliminamos la imagen si existe
                if (imageUrl.isNotEmpty()) {
                    storageRepository.deleteRecipeImage(imageUrl)
                }

                // Luego eliminamos la receta
                val success = recipeRepository.deleteRecipe(recipeId)

                // Actualizar listas
                loadRecipes()
                loadFavoriteRecipes()

                onComplete(success)
            } catch (e: Exception) {
                errorMessage = "Error al eliminar receta: ${e.message}"
                onComplete(false)
            } finally {
                isLoading = false
            }
        }
    }
}