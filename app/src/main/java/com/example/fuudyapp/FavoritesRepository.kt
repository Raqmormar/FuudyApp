package com.example.fuudyapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Repositorio para gestionar las recetas favoritas de los usuarios
 * Maneja operaciones CRUD para favoritos en Firestore
 */
class FavoritesRepository {
    // Instancias de Firebase para base de datos y autenticación
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Propiedad computada que obtiene el ID del usuario actual
    // Lanza excepción si no hay usuario autenticado (seguridad)
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    /**
     * Obtiene la referencia al documento de favoritos del usuario actual
     * Estructura: userFavorites/{userId} -> { favoriteRecipes: [recipeId1, recipeId2...] }
     */
    private fun getUserFavoritesRef() =
        firestore.collection("userFavorites").document(currentUserId)

    /**
     * Añade una receta a la lista de favoritos del usuario
     * @param recipeId ID de la receta a agregar
     * @param onSuccess Callback ejecutado cuando la operación es exitosa
     * @param onError Callback ejecutado si hay error
     */
    fun addToFavorites(recipeId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        getUserFavoritesRef()
            // arrayUnion añade el elemento al array sin duplicados
            .update("favoriteRecipes", FieldValue.arrayUnion(recipeId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                // Manejo especial: si el documento no existe, créalo
                if (exception is FirebaseFirestoreException &&
                    exception.code == FirebaseFirestoreException.Code.NOT_FOUND) {

                    // Crear documento nuevo con la primera receta favorita
                    getUserFavoritesRef()
                        .set(mapOf("favoriteRecipes" to listOf(recipeId)))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                } else {
                    // Otros errores se pasan directamente
                    onError(exception)
                }
            }
    }

    /**
     * Elimina una receta de la lista de favoritos del usuario
     * @param recipeId ID de la receta a eliminar
     * @param onSuccess Callback ejecutado cuando la operación es exitosa
     * @param onError Callback ejecutado si hay error
     */
    fun removeFromFavorites(recipeId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        getUserFavoritesRef()
            // arrayRemove elimina todas las instancias del elemento del array
            .update("favoriteRecipes", FieldValue.arrayRemove(recipeId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    /**
     * Verifica si una receta específica está en favoritos del usuario
     * @param recipeId ID de la receta a verificar
     * @param onResult Callback con resultado boolean (true si está en favoritos)
     */
    fun isRecipeFavorite(recipeId: String, onResult: (Boolean) -> Unit) {
        getUserFavoritesRef().get()
            .addOnSuccessListener { document ->
                // Extrae la lista de favoritos, usa lista vacía si no existe
                val favorites = document.get("favoriteRecipes") as? List<String> ?: emptyList()
                // Verifica si el ID está en la lista
                onResult(recipeId in favorites)
            }
            .addOnFailureListener {
                // En caso de error, asume que no está en favoritos
                onResult(false)
            }
    }

    /**
     * Obtiene la lista completa de IDs de recetas favoritas del usuario
     * @param onSuccess Callback con la lista de IDs de recetas favoritas
     * @param onError Callback ejecutado si hay error
     */
    fun getFavoriteRecipes(onSuccess: (List<String>) -> Unit, onError: (Exception) -> Unit) {
        getUserFavoritesRef().get()
            .addOnSuccessListener { document ->
                // Extrae la lista de favoritos del documento
                val favorites = document.get("favoriteRecipes") as? List<String> ?: emptyList()
                onSuccess(favorites)
            }
            .addOnFailureListener { onError(it) }
    }
}