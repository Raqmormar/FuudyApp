package com.example.fuudyapp.data.repository

import com.example.fuudyapp.data.FirebaseManager
import com.example.fuudyapp.models.Recipe
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para gestionar operaciones CRUD de recetas en Firebase
 * Maneja toda la lógica de acceso a datos para recetas
 */
class RecipeRepository {

    suspend fun getAllRecipes(): List<Recipe> {
        return try {
            // Consulta todos los documentos de la colección "recipes"
            val querySnapshot = FirebaseManager.recipesCollection.get().await()
            // Convierte cada documento a objeto Recipe
            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            // En caso de error, retorna lista vacía
            emptyList()
        }
    }
    /**
     * Obtiene una receta específica por su ID
     * @param recipeId ID de la receta a buscar
     * @return Objeto Recipe o null si no se encuentra/hay error
     */
    suspend fun getRecipeById(recipeId: String): Recipe? {
        return try {
            val documentSnapshot = FirebaseManager.recipesCollection.document(recipeId).get().await()
            documentSnapshot.toObject(Recipe::class.java)?.copy(id = documentSnapshot.id)
        } catch (e: Exception) {
            null
        }
    }
    /**
     * Obtiene múltiples recetas favoritas basadas en una lista de IDs
     * @param favoriteIds Lista de IDs de recetas favoritas
     * @return Lista de recetas favoritas
     */
    suspend fun getFavoriteRecipes(favoriteIds: List<String>): List<Recipe> {
        if (favoriteIds.isEmpty()) return emptyList()

        return try {
            val recipes = mutableListOf<Recipe>()

            // Firestore no permite directamente consultas con más de 10 IDs en un where in
            // Así que debemos dividir la consulta si hay muchos favoritos
            favoriteIds.chunked(10).forEach { chunk ->
                val querySnapshot = FirebaseManager.recipesCollection
                    .whereIn("__name__", chunk)
                    .get()
                    .await()
                // Añade los resultados de cada chunk a la lista principal
                recipes.addAll(querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Recipe::class.java)?.copy(id = doc.id)
                })
            }

            recipes
        } catch (e: Exception) {
            emptyList()
        }
    }
    /**
     * Añade una nueva receta a la base de datos
     * @param recipe Objeto Recipe a guardar
     * @return ID del documento creado o null en caso de error
     */
    suspend fun addRecipe(recipe: Recipe): String? {
        return try {
            val docRef = FirebaseManager.recipesCollection.add(recipe).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }
    /**
     * Actualiza una receta existente
     * @param recipe Objeto Recipe con datos actualizados (debe tener ID válido)
     * @return true si se actualizó correctamente, false en caso contrario
     */
    suspend fun updateRecipe(recipe: Recipe): Boolean {
        if (recipe.id.isEmpty()) return false

        return try {
            FirebaseManager.recipesCollection.document(recipe.id).set(recipe).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    /**
     * Elimina una receta de la base de datos
     * @param recipeId ID de la receta a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            FirebaseManager.recipesCollection.document(recipeId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
    /**
     * Busca recetas por texto usando múltiples criterios
     * @param query Texto de búsqueda
     * @return Lista de recetas que coinciden con la búsqueda
     */
    suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            // BÚSQUEDA POR TAGS: Busca en un array de etiquetas
            val querySnapshot = FirebaseManager.recipesCollection
                .whereArrayContains("tags", query.lowercase())
                .get()
                .await()

            val byTags = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            }

            // BÚSQUEDA POR TÍTULO: Búsqueda por prefijo en el nombre
            val titleSnapshot = FirebaseManager.recipesCollection
                .orderBy("name")
                .startAt(query)
                .endAt(query + '\uf8ff')
                .get()
                .await()

            val byTitle = titleSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            }

            // Combinamos los resultados y eliminamos duplicados
            (byTags + byTitle).distinctBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
}