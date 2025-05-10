package com.example.fuudyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fuudyapp.ui.theme.FuudyApp
import com.example.fuudyapp.ui.theme.HomeScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            FuudyApp {
                // Estructura principal
                AppNavigation()
            }
        }
    }
}