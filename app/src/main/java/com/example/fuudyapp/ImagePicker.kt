package com.example.fuudyapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Componente para seleccionar imágenes desde la galería
 * @param currentImageUrl - URL de imagen existente (para edición)
 * @param onImageSelected - Callback cuando se selecciona/elimina imagen
 */
@Composable
fun ImagePicker(
    selectedImageUri: Uri? = null, // Cambiado: ahora recibe el URI como parámetro
    currentImageUrl: String = "",
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current

    // Launcher para abrir galería y seleccionar imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri) // Directamente notifica al padre
    }

    // Colores de tu app
    val primaryGreen = Color(0xFF355E37)
    val lightGray = Color(0xFFF5F5F5)

    // Contenedor principal clickeable
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(lightGray)
            .clickable { galleryLauncher.launch("image/*") }, // Cambiado a solo imágenes
        contentAlignment = Alignment.Center
    ) {
        // Si hay imagen seleccionada o URL existente
        if (selectedImageUri != null || currentImageUrl.isNotEmpty()) {
            // Contenedor para imagen y botón eliminar
            Box(modifier = Modifier.fillMaxSize()) {
                // AsyncImage simple sin placeholder ni error
                AsyncImage(
                    model = selectedImageUri ?: currentImageUrl,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Botón eliminar (esquina superior derecha)
                IconButton(
                    onClick = {
                        onImageSelected(null) // Notifica al padre que se eliminó la imagen
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Image",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Estado vacío: mostrar indicadores para agregar imagen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(40.dp),
                    tint = primaryGreen.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap to add recipe image",
                    color = primaryGreen.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "From gallery",
                    color = primaryGreen.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}