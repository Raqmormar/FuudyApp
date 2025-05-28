package com.example.fuudyapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

/**
 * Componente para seleccionar imágenes desde la galería
 * @param currentImageUrl - URL de imagen existente (para edición)
 * @param onImageSelected - Callback cuando se selecciona/elimina imagen
 */
@Composable
fun ImagePicker(
    currentImageUrl: String = "",
    onImageSelected: (Uri?) -> Unit
) {
    // Estado para almacenar URI de imagen seleccionada
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Launcher para abrir galería y seleccionar imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        onImageSelected(uri)
    }

    // Contenedor principal clickeable
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .clickable { galleryLauncher.launch("image/*") }, // Abre galería solo para imágenes
        contentAlignment = Alignment.Center
    ) {
        // Si hay imagen seleccionada o URL existente
        if (selectedImageUri != null || currentImageUrl.isNotEmpty()) {
            // Contenedor para imagen y botón eliminar
            Box(modifier = Modifier.fillMaxSize()) {
                // Imagen principal
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(selectedImageUri ?: currentImageUrl) // Prioriza imagen seleccionada
                            .build()
                    ),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Recorta manteniendo proporción
                )

                // Botón eliminar (esquina superior derecha)
                IconButton(
                    onClick = {
                        selectedImageUri = null
                        onImageSelected(null)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Image",
                        tint = Color.Black
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
                    tint = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Click to add an image",
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}