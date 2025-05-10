package com.example.fuudyapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Clase singleton para proporcionar acceso centralizado a los servicios de Firebase
 */
object FirebaseManager {
    // Referencias a servicios de Firebase
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Colecciones de Firestore
    val usersCollection = firestore.collection("users")
    val recipesCollection = firestore.collection("recipes")
}