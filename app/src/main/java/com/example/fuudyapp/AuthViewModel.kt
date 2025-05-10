package com.example.fuudyapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuudyapp.data.FirebaseManager
import com.example.fuudyapp.data.repository.UserRepository
import com.example.fuudyapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    // Estado UI usando Compose State
    var authState by mutableStateOf<AuthState>(AuthState.Initial)
        private set

    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    init {
        // Forzar cierre de sesión para asegurar que se muestra la pantalla de login
        auth.signOut()

        // Verificar estado de autenticación al iniciar
        currentUser = auth.currentUser
        authState = if (currentUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun register(email: String, password: String, displayName: String = "") {
        authState = AuthState.Loading
        viewModelScope.launch {
            try {
                // Crear usuario en Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                currentUser = authResult.user

                // Crear perfil de usuario en Firestore
                val userId = authResult.user?.uid ?: return@launch
                val user = User(
                    id = userId,       // Cambiar de id a uid para coincidir con la clase User
                    email = email,
                    displayName = displayName,
                    createdAt = System.currentTimeMillis()
                )

                userRepository.updateUserProfile(user)

                authState = AuthState.Authenticated
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                currentUser = authResult.user
                authState = AuthState.Authenticated
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
        authState = AuthState.Unauthenticated
    }

    // Estados de autenticación
    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }
}