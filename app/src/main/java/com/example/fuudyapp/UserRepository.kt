package com.example.fuudyapp.data.repository

import com.example.fuudyapp.data.FirebaseManager
import com.example.fuudyapp.models.User
import kotlinx.coroutines.tasks.await

class UserRepository {

    suspend fun getCurrentUserProfile(): User? {
        val userId = FirebaseManager.auth.currentUser?.uid ?: return null

        return try {
            val documentSnapshot = FirebaseManager.usersCollection.document(userId).get().await()
            documentSnapshot.toObject(User::class.java)?.copy(id = userId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(user: User): Boolean {
        return try {
            FirebaseManager.usersCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleFavoriteRecipe(recipeId: String): Boolean {
        val userId = FirebaseManager.auth.currentUser?.uid ?: return false

        return try {
            // Referencia a la colección userFavorites
            val userFavoritesRef = FirebaseManager.firestore
                .collection("userFavorites")
                .document(userId)

            // Verificar si el documento existe y obtener los favoritos actuales
            val documentSnapshot = userFavoritesRef.get().await()

            if (documentSnapshot.exists()) {
                // El documento existe, obtener lista actual de favoritos
                val favorites = documentSnapshot.get("favoriteRecipes") as? List<String> ?: emptyList()

                // Actualizar la lista según corresponda
                if (recipeId in favorites) {
                    // Eliminar de favoritos
                    userFavoritesRef.update(
                        "favoriteRecipes", favorites.filter { it != recipeId },
                        "lastUpdated", com.google.firebase.Timestamp.now()
                    ).await()
                } else {
                    // Añadir a favoritos
                    userFavoritesRef.update(
                        "favoriteRecipes", favorites + recipeId,
                        "lastUpdated", com.google.firebase.Timestamp.now()
                    ).await()
                }
            } else {
                // El documento no existe, crearlo
                val data = mapOf(
                    "favoriteRecipes" to listOf(recipeId),
                    "lastUpdated" to com.google.firebase.Timestamp.now()
                )
                userFavoritesRef.set(data).await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFavoriteRecipes(): List<String> {
        val userId = FirebaseManager.auth.currentUser?.uid ?: return emptyList()

        return try {
            // Obtener favoritos de la colección userFavorites
            val documentSnapshot = FirebaseManager.firestore
                .collection("userFavorites")
                .document(userId)
                .get()
                .await()

            // Extraer la lista de favoritos
            documentSnapshot.get("favoriteRecipes") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}