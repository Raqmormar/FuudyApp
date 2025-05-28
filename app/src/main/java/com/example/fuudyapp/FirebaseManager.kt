package com.example.fuudyapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Clase singleton para proporcionar acceso centralizado a los servicios de Firebase
 * Patrón Singleton: garantiza una sola instancia en toda la aplicación
 */
object FirebaseManager {
    // Referencias a servicios de Firebase usando lazy initialization
    // lazy: inicializa el objeto solo cuando se accede por primera vez (optimización)

    // Servicio de autenticación de Firebase (login, registro, logout)
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Base de datos NoSQL de Firebase para almacenar documentos
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Servicio de almacenamiento de archivos (imágenes, videos, etc.)
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Referencias pre-configuradas a colecciones principales de Firestore
    // Evita repetir los nombres de colecciones en toda la app

    // Colección para almacenar perfiles de usuario
    val usersCollection = firestore.collection("users")

    // Colección para almacenar recetas de cocina
    val recipesCollection = firestore.collection("recipes")
}