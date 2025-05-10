package com.example.fuudyapp.data.repository

import android.net.Uri
import com.example.fuudyapp.data.FirebaseManager
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {
    private val storageRef = FirebaseManager.storage.reference

    suspend fun uploadRecipeImage(imageUri: Uri, recipeId: String): String? {
        return try {
            // Crear referencia a la imagen con una ruta específica y un nombre único
            val imageRef = storageRef.child("recipes/$recipeId/${UUID.randomUUID()}.jpg")

            // Subir la imagen y esperar a que se complete
            imageRef.putFile(imageUri).await()

            // Obtener y devolver la URL de descarga
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteRecipeImage(imageUrl: String): Boolean {
        return try {
            // Obtener referencia a partir de la URL
            val imageRef = FirebaseManager.storage.getReferenceFromUrl(imageUrl)

            // Eliminar la imagen
            imageRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}