package com.example.fuudyapp.data.repository

import android.net.Uri
import com.example.fuudyapp.data.FirebaseManager
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repositorio para gestionar la subida y eliminación de imágenes en Firebase Storage
 * Maneja específicamente las imágenes de recetas
 */
class StorageRepository {
    // Referencia al Storage de Firebase desde FirebaseManager
    private val storageRef = FirebaseManager.storage.reference

    /**
     * Sube una imagen de receta a Firebase Storage
     * @param imageUri URI local de la imagen seleccionada por el usuario
     * @param recipeId ID de la receta para organizar las imágenes por carpetas
     * @return URL de descarga de la imagen subida o null si falla
     */
    suspend fun uploadRecipeImage(imageUri: Uri, recipeId: String): String? {
        return try {
            // CREAR RUTA ORGANIZADA: recipes/{recipeId}/{UUID}.jpg
            // - recipes/: carpeta principal para todas las imágenes de recetas
            // - {recipeId}/: subcarpeta específica para cada receta
            // - {UUID}.jpg: nombre único para evitar conflictos
            val imageRef = storageRef.child("recipes/$recipeId/${UUID.randomUUID()}.jpg")

            // SUBIR ARCHIVO: putFile() toma un URI local y lo sube a Firebase
            // await() convierte la Task de Firebase en función suspendida
            imageRef.putFile(imageUri).await()

            // OBTENER URL DE DESCARGA: necesaria para mostrar la imagen en la app
            // Esta URL se puede usar desde cualquier parte de la app para cargar la imagen
            imageRef.downloadUrl.await().toString()

        } catch (e: Exception) {
            // En caso de error (sin conexión, permisos, etc.), retorna null
            null
        }
    }

    /**
     * Elimina una imagen de receta de Firebase Storage
     * @param imageUrl URL completa de la imagen a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    suspend fun deleteRecipeImage(imageUrl: String): Boolean {
        return try {
            // OBTENER REFERENCIA desde URL: Firebase puede crear una referencia
            // desde la URL completa de descarga
            val imageRef = FirebaseManager.storage.getReferenceFromUrl(imageUrl)

            // ELIMINAR ARCHIVO: delete() elimina permanentemente el archivo
            imageRef.delete().await()

            true
        } catch (e: Exception) {
            // Error al eliminar (archivo no existe, sin permisos, etc.)
            false
        }
    }
}