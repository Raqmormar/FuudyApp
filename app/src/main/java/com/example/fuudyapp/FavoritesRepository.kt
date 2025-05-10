package com.example.fuudyapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class FavoritesRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtener el ID del usuario actual
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    // Referencia a la colección de favoritos del usuario
    private fun getUserFavoritesRef() =
        firestore.collection("userFavorites").document(currentUserId)

    // Añadir una receta a favoritos
    fun addToFavorites(recipeId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        getUserFavoritesRef()
            .update("favoriteRecipes", FieldValue.arrayUnion(recipeId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                // Si el documento no existe, créalo
                if (exception is FirebaseFirestoreException &&
                    exception.code == FirebaseFirestoreException.Code.NOT_FOUND) {

                    getUserFavoritesRef()
                        .set(mapOf("favoriteRecipes" to listOf(recipeId)))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                } else {
                    onError(exception)
                }
            }
    }

    // Eliminar una receta de favoritos
    fun removeFromFavorites(recipeId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        getUserFavoritesRef()
            .update("favoriteRecipes", FieldValue.arrayRemove(recipeId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // Verificar si una receta está en favoritos
    fun isRecipeFavorite(recipeId: String, onResult: (Boolean) -> Unit) {
        getUserFavoritesRef().get()
            .addOnSuccessListener { document ->
                val favorites = document.get("favoriteRecipes") as? List<String> ?: emptyList()
                onResult(recipeId in favorites)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // Obtener todas las recetas favoritas del usuario
    fun getFavoriteRecipes(onSuccess: (List<String>) -> Unit, onError: (Exception) -> Unit) {
        getUserFavoritesRef().get()
            .addOnSuccessListener { document ->
                val favorites = document.get("favoriteRecipes") as? List<String> ?: emptyList()
                onSuccess(favorites)
            }
            .addOnFailureListener { onError(it) }
    }
}