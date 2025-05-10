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

@Composable
fun ImagePicker(
    currentImageUrl: String = "",
    onImageSelected: (Uri?) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        onImageSelected(uri)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .clickable { galleryLauncher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null || currentImageUrl.isNotEmpty()) {
            // Mostrar imagen seleccionada o la existente
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(selectedImageUri ?: currentImageUrl)
                            .build()
                    ),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Botón para eliminar la imagen
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
            // Mostrar indicación para seleccionar imagen
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