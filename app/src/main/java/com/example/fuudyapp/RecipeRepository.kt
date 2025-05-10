package com.example.fuudyapp.data.repository

import com.example.fuudyapp.data.FirebaseManager
import com.example.fuudyapp.models.Recipe
import kotlinx.coroutines.tasks.await

class RecipeRepository {

    suspend fun getAllRecipes(): List<Recipe> {
        return try {
            val querySnapshot = FirebaseManager.recipesCollection.get().await()
            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeById(recipeId: String): Recipe? {
        return try {
            val documentSnapshot = FirebaseManager.recipesCollection.document(recipeId).get().await()
            documentSnapshot.toObject(Recipe::class.java)?.copy(id = documentSnapshot.id)
        } catch (e: Exception) {
            null
        }
    }

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

                recipes.addAll(querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Recipe::class.java)?.copy(id = doc.id)
                })
            }

            recipes
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addRecipe(recipe: Recipe): String? {
        return try {
            val docRef = FirebaseManager.recipesCollection.add(recipe).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRecipe(recipe: Recipe): Boolean {
        if (recipe.id.isEmpty()) return false

        return try {
            FirebaseManager.recipesCollection.document(recipe.id).set(recipe).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            FirebaseManager.recipesCollection.document(recipeId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            // Firebase no tiene full-text search nativo, así que hacemos una búsqueda básica
            val querySnapshot = FirebaseManager.recipesCollection
                .whereArrayContains("tags", query.lowercase())
                .get()
                .await()

            val byTags = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            }

            // También buscamos por título (esto no es muy eficiente para grandes colecciones)
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