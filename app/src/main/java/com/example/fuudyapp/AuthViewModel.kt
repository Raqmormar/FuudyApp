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

/**
 * ViewModel para manejar la autenticación de usuarios en Fuudy
 * Gestiona login, registro, logout y estados de autenticación
 */
class AuthViewModel : ViewModel() {

    // Instancias de Firebase para autenticación y gestión de usuarios
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    // ESTADOS OBSERVABLES PARA LA UI

    // Estado actual de la autenticación (loading, autenticado, error, etc.)
    var authState by mutableStateOf<AuthState>(AuthState.Initial)
        private set // Solo este ViewModel puede modificar el estado

    // Usuario actualmente autenticado (null si no hay sesión activa)
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set // Solo lectura desde la UI

    // INICIALIZACIÓN DEL VIEWMODEL
    init {
        // Fuerza el cierre de sesión para testing/desarrollo
        // NOTA: En producción, esto debería eliminarse
        auth.signOut()

        // Verifica si ya hay un usuario autenticado al iniciar la app
        currentUser = auth.currentUser
        authState = if (currentUser != null) {
            AuthState.Authenticated    // Usuario ya logueado
        } else {
            AuthState.Unauthenticated  // Necesita hacer login
        }
    }

    /**
     * Registra un nuevo usuario en la aplicación
     * @param email Email del usuario
     * @param password Contraseña
     * @param displayName Nombre para mostrar (opcional)
     */
    fun register(email: String, password: String, displayName: String = "") {
        authState = AuthState.Loading // Indica que está procesando

        viewModelScope.launch { // Ejecuta en corrutina para operación asíncrona
            try {
                // 1. Crear cuenta en Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                currentUser = authResult.user

                // 2. Crear perfil completo del usuario en Firestore
                val userId = authResult.user?.uid ?: return@launch // Sale si no hay UID
                val user = User(
                    id = userId,       // Usar UID de Firebase Auth
                    email = email,
                    displayName = displayName,
                    createdAt = System.currentTimeMillis() // Timestamp de creación
                )

                // 3. Guardar perfil en base de datos
                userRepository.updateUserProfile(user)

                // 4. Marcar como autenticado exitosamente
                authState = AuthState.Authenticated

            } catch (e: Exception) {
                // Manejo de errores durante el registro
                authState = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    /**
     * Inicia sesión con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña
     */
    fun login(email: String, password: String) {
        authState = AuthState.Loading // Muestra indicador de carga

        viewModelScope.launch { // Operación asíncrona
            try {
                // Intenta autenticar con Firebase
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                currentUser = authResult.user
                authState = AuthState.Authenticated

            } catch (e: Exception) {
                // Manejo de errores de login (credenciales incorrectas, etc.)
                authState = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    /**
     * Cierra la sesión del usuario actual
     * Limpia todos los estados de autenticación
     */
    fun logout() {
        auth.signOut()                        // Cierra sesión en Firebase
        currentUser = null                    // Limpia usuario local
        authState = AuthState.Unauthenticated // Actualiza estado
    }

    /**
     * Estados posibles de la autenticación
     * Sealed class garantiza que todos los casos están cubiertos
     */
    sealed class AuthState {
        object Initial : AuthState()          // Estado inicial al abrir la app
        object Loading : AuthState()          // Procesando login/registro
        object Authenticated : AuthState()    // Usuario logueado correctamente
        object Unauthenticated : AuthState()  // Usuario necesita hacer login
        data class Error(val message: String) : AuthState() // Error con mensaje específico
    }
}