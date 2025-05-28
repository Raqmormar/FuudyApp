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

/**
 * ViewModel principal para gestionar el estado de las recetas
 * Centraliza toda la lógica de negocio relacionada con recetas y favoritos
 */
class RecipeViewModel : ViewModel() {

    // REPOSITORIOS: Instancias para acceso a datos
    private val recipeRepository = RecipeRepository()
    private val userRepository = UserRepository()
    private val storageRepository = StorageRepository()
    private val favoritesRepository = FavoritesRepository()

    // ESTADO DE RECETAS: StateFlow para observación reactiva desde la UI
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    // ESTADO DE FAVORITOS: Lista de recetas marcadas como favoritas
    private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes.asStateFlow()

    // ESTADO DE IDS FAVORITOS: Set de IDs para verificación rápida
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // ESTADOS DE UI: Variables observables con mutableStateOf para Compose
    var isLoading by mutableStateOf(false)
        private set // Solo este ViewModel puede modificar el estado

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // ESTADOS PARA CARGA DE IMÁGENES
    var isUploading by mutableStateOf(false)
        private set

    var uploadProgress by mutableStateOf(0f) // Progreso de 0.0 a 1.0
        private set

    // INICIALIZACIÓN: Carga datos al crear el ViewModel
    init {
        loadRecipes()
        loadFavoriteRecipes()
    }

    /**
     * Carga todas las recetas desde Firebase
     */
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

    /**
     * Carga las recetas favoritas del usuario actual
     * Usa callback pattern del FavoritesRepository
     */
    fun loadFavoriteRecipes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Usar FavoritesRepository con callbacks
                favoritesRepository.getFavoriteRecipes(
                    onSuccess = { favoriteIds ->
                        // Actualizar set de IDs para verificación rápida
                        _favoriteIds.value = favoriteIds.toSet()

                        if (favoriteIds.isNotEmpty()) {
                            // Cargar detalles completos de las recetas favoritas
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
                            // Sin favoritos
                            _favoriteRecipes.value = emptyList()
                        }

                        isLoading = false
                    },
                    onError = { e ->
                        // Manejo de errores del callback
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

    /**
     * Alterna el estado de favorito de una receta
     * @param recipeId ID de la receta a alternar
     */
    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            try {
                // Verificar estado actual de favorito
                val isFavorite = _favoriteIds.value.contains(recipeId)

                if (isFavorite) {
                    // ELIMINAR DE FAVORITOS
                    favoritesRepository.removeFromFavorites(
                        recipeId,
                        onSuccess = {
                            // Actualizar estado local inmediatamente para UI responsiva
                            _favoriteIds.value = _favoriteIds.value - recipeId
                            _favoriteRecipes.value = _favoriteRecipes.value.filter { it.id != recipeId }
                        },
                        onError = { e ->
                            errorMessage = "Error removing from favorites: ${e.message}"
                        }
                    )
                } else {
                    // AÑADIR A FAVORITOS
                    favoritesRepository.addToFavorites(
                        recipeId,
                        onSuccess = {
                            // Actualizar estado local inmediatamente
                            _favoriteIds.value = _favoriteIds.value + recipeId

                            // Si tenemos la receta en memoria, añadirla a favoritos
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

    /**
     * Busca recetas por texto
     * @param query Texto de búsqueda
     */
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                if (query.isBlank()) {
                    // Si no hay query, cargar todas las recetas
                    loadRecipes()
                } else {
                    // Buscar recetas que coincidan con el query
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

    /**
     * Obtiene una receta específica por ID
     * @param recipeId ID de la receta
     * @param onResult Callback con el resultado
     */
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

    /**
     * Añade una nueva receta, incluyendo la subida de imagen si existe
     * @param recipe Datos de la receta
     * @param imageUri URI de la imagen seleccionada (opcional)
     * @param onComplete Callback con resultado de éxito/error
     */
    fun addRecipeWithImage(recipe: Recipe, imageUri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isUploading = true
            uploadProgress = 0f
            errorMessage = null

            try {
                // PASO 1: Crear receta sin imagen para obtener ID
                val tempRecipe = recipe.copy(imageUrl = "")
                val recipeId = recipeRepository.addRecipe(tempRecipe)

                if (recipeId != null) {
                    var imageUrl = ""

                    // PASO 2: Subir imagen si existe
                    if (imageUri != null) {
                        uploadProgress = 0.3f
                        imageUrl = storageRepository.uploadRecipeImage(imageUri, recipeId) ?: ""
                        uploadProgress = 0.7f
                    }

                    // PASO 3: Actualizar receta con URL de imagen
                    val updatedRecipe = recipe.copy(id = recipeId, imageUrl = imageUrl)
                    val success = recipeRepository.updateRecipe(updatedRecipe)
                    uploadProgress = 1f

                    // PASO 4: Refrescar lista para mostrar nueva receta
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
                // Resetear estados de carga
                isUploading = false
                uploadProgress = 0f
            }
        }
    }

    /**
     * Actualiza una receta existente, manejando cambios de imagen
     * @param recipe Datos actualizados de la receta
     * @param imageUri Nueva imagen seleccionada (opcional)
     * @param onComplete Callback con resultado
     */
    fun updateRecipeWithImage(recipe: Recipe, imageUri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isUploading = true
            uploadProgress = 0f
            errorMessage = null

            try {
                var imageUrl = recipe.imageUrl

                // Si hay nueva imagen seleccionada
                if (imageUri != null) {
                    uploadProgress = 0.2f

                    // Eliminar imagen anterior si existe
                    if (imageUrl.isNotEmpty()) {
                        storageRepository.deleteRecipeImage(imageUrl)
                    }

                    // Subir nueva imagen
                    uploadProgress = 0.5f
                    imageUrl = storageRepository.uploadRecipeImage(imageUri, recipe.id) ?: ""
                    uploadProgress = 0.8f
                }

                // Actualizar receta con nueva URL
                val updatedRecipe = recipe.copy(imageUrl = imageUrl)
                val success = recipeRepository.updateRecipe(updatedRecipe)
                uploadProgress = 1f

                // Refrescar datos para mostrar cambios
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

    /**
     * Elimina una receta y su imagen asociada
     * @param recipeId ID de la receta a eliminar
     * @param imageUrl URL de la imagen a eliminar
     * @param onComplete Callback con resultado
     */
    fun deleteRecipeWithImage(recipeId: String, imageUrl: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // PASO 1: Eliminar imagen de Firebase Storage
                if (imageUrl.isNotEmpty()) {
                    storageRepository.deleteRecipeImage(imageUrl)
                }

                // PASO 2: Eliminar receta de Firestore
                val success = recipeRepository.deleteRecipe(recipeId)

                // PASO 3: Actualizar listas locales
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