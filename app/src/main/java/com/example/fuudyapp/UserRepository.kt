package com.example.fuudyapp.data.repository

import com.example.fuudyapp.data.FirebaseManager
import com.example.fuudyapp.models.User
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para gestionar operaciones relacionadas con usuarios y sus favoritos
 * Maneja perfiles de usuario y sistema de recetas favoritas
 */
class UserRepository {

    /**
     * Obtiene el perfil del usuario actualmente autenticado
     * @return Objeto User con la información del perfil o null si no existe/hay error
     */
    suspend fun getCurrentUserProfile(): User? {
        // Obtener UID del usuario autenticado actual
        val userId = FirebaseManager.auth.currentUser?.uid ?: return null

        return try {
            // Buscar documento del usuario en la colección "users"
            val documentSnapshot = FirebaseManager.usersCollection.document(userId).get().await()

            // Convertir documento a objeto User y asignar el ID
            documentSnapshot.toObject(User::class.java)?.copy(id = userId)
        } catch (e: Exception) {
            // Retorna null en caso de error (sin conexión, permisos, etc.)
            null
        }
    }

    /**
     * Actualiza o crea el perfil de un usuario
     * @param user Objeto User con los datos a guardar
     * @return true si se guardó correctamente, false en caso contrario
     */
    suspend fun updateUserProfile(user: User): Boolean {
        return try {
            // set() crea o actualiza completamente el documento del usuario
            FirebaseManager.usersCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Alterna el estado de favorito de una receta para el usuario actual
     * Si está en favoritos la elimina, si no está la añade
     * @param recipeId ID de la receta a alternar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun toggleFavoriteRecipe(recipeId: String): Boolean {
        // Verificar que hay un usuario autenticado
        val userId = FirebaseManager.auth.currentUser?.uid ?: return false

        return try {
            // REFERENCIA AL DOCUMENTO DE FAVORITOS del usuario
            // Estructura: userFavorites/{userId} -> { favoriteRecipes: [id1, id2...] }
            val userFavoritesRef = FirebaseManager.firestore
                .collection("userFavorites")
                .document(userId)

            // OBTENER ESTADO ACTUAL del documento de favoritos
            val documentSnapshot = userFavoritesRef.get().await()

            if (documentSnapshot.exists()) {
                // DOCUMENTO EXISTE: Actualizar lista existente

                // Extraer lista actual de favoritos (o lista vacía si no existe el campo)
                val favorites = documentSnapshot.get("favoriteRecipes") as? List<String> ?: emptyList()

                // LÓGICA DE TOGGLE: añadir o eliminar según estado actual
                if (recipeId in favorites) {
                    // ELIMINAR: La receta ya está en favoritos
                    userFavoritesRef.update(
                        "favoriteRecipes", favorites.filter { it != recipeId }, // Nueva lista sin la receta
                        "lastUpdated", com.google.firebase.Timestamp.now() // Timestamp de modificación
                    ).await()
                } else {
                    // AÑADIR: La receta no está en favoritos
                    userFavoritesRef.update(
                        "favoriteRecipes", favorites + recipeId, // Nueva lista con la receta añadida
                        "lastUpdated", com.google.firebase.Timestamp.now()
                    ).await()
                }
            } else {
                // DOCUMENTO NO EXISTE: Crear nuevo documento
                val data = mapOf(
                    "favoriteRecipes" to listOf(recipeId), // Primera receta favorita
                    "lastUpdated" to com.google.firebase.Timestamp.now() // Timestamp de creación
                )
                userFavoritesRef.set(data).await()
            }

            true
        } catch (e: Exception) {
            // Error en cualquier operación de Firestore
            false
        }
    }

    /**
     * Obtiene la lista de IDs de recetas favoritas del usuario actual
     * @return Lista de IDs de recetas favoritas o lista vacía si no hay/hay error
     */
    suspend fun getFavoriteRecipes(): List<String> {
        // Verificar que hay un usuario autenticado
        val userId = FirebaseManager.auth.currentUser?.uid ?: return emptyList()

        return try {
            // OBTENER DOCUMENTO de favoritos del usuario
            val documentSnapshot = FirebaseManager.firestore
                .collection("userFavorites")
                .document(userId)
                .get()
                .await()

            // EXTRAER LISTA de favoritos del campo "favoriteRecipes"
            // Si el campo no existe o no es una lista, retorna lista vacía
            documentSnapshot.get("favoriteRecipes") as? List<String> ?: emptyList()

        } catch (e: Exception) {
            // En caso de error, retorna lista vacía
            emptyList()
        }
    }
}