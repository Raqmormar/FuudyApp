package com.example.fuudyapp

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fuudyapp.models.Recipe
import com.example.fuudyapp.ui.components.ImagePicker
import com.example.fuudyapp.ui.viewmodel.RecipeViewModel

/**
 * Pantalla para agregar o editar recetas
 * @param navController - Control de navegación
 * @param recipeId - ID de receta (null para nueva receta)
 * @param recipeViewModel - ViewModel para gestionar datos de recetas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    navController: NavHostController,
    recipeId: String? = null,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    // Paleta de colores de la aplicación
    val primaryGreen = Color(0xFF355E37)
    val lightGray = Color(0xFFF5F5F5)
    val backgroundColor = Color(0xFFF8F7F5)
    val cardBackground = Color.White

    // Estado de scroll vertical para toda la pantalla
    val scrollState = rememberScrollState()

    // Estados para todos los campos del formulario
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Breakfast") }
    var prepTime by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Easy") }
    var ingredientsText by remember { mutableStateOf("") }
    var instructionsText by remember { mutableStateOf("") }

    // Estados para manejo de imágenes
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Opciones predefinidas para dropdowns y chips
    val categories = listOf("Breakfast", "Lunch", "Dinner", "Desserts", "Healthy")
    val difficulties = listOf("Easy", "Medium", "Hard")

    // Carga datos de receta existente si es modo edición
    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            recipeViewModel.getRecipeById(recipeId) { recipe ->
                recipe?.let {
                    name = it.name
                    description = it.description
                    category = it.category
                    prepTime = it.prepTime
                    difficulty = it.difficulty
                    ingredientsText = it.ingredients.joinToString("\n")
                    instructionsText = it.instructions.joinToString("\n")
                    imageUrl = it.imageUrl
                }
            }
        }
    }

    // Contenedor principal con imagen de fondo
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo fija
        Image(
            painter = painterResource(id = R.drawable.bck),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Scaffold con barra superior y contenido
        Scaffold(
            containerColor = Color.Transparent, // Transparente para ver fondo
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor.copy(alpha = 0.85f) // Semi-transparente
                    ),
                    title = {
                        Text(
                            text = if (recipeId == null) "Add Recipe" else "Edit Recipe",
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen
                        )
                    },
                    navigationIcon = {
                        // Botón de retroceso
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryGreen
                            )
                        }
                    },
                    actions = {
                        // Botón de guardar en la barra superior
                        IconButton(
                            onClick = {
                                // Procesa y guarda la receta
                                val ingredients = ingredientsText
                                    .split("\n")
                                    .filter { it.isNotBlank() }
                                    .map { it.trim() }

                                val instructions = instructionsText
                                    .split("\n")
                                    .filter { it.isNotBlank() }
                                    .map { it.trim() }

                                val recipe = Recipe(
                                    id = recipeId ?: "",
                                    name = name,
                                    description = description,
                                    category = category,
                                    ingredients = ingredients,
                                    instructions = instructions,
                                    imageUrl = imageUrl,
                                    prepTime = prepTime,
                                    difficulty = difficulty
                                )

                                // Decide si agregar nueva receta o actualizar existente
                                if (recipeId == null) {
                                    recipeViewModel.addRecipeWithImage(recipe, selectedImageUri) { success ->
                                        if (success) {
                                            navController.navigateUp()
                                        }
                                    }
                                } else {
                                    recipeViewModel.updateRecipeWithImage(recipe, selectedImageUri) { success ->
                                        if (success) {
                                            navController.navigateUp()
                                        }
                                    }
                                }
                            },
                            enabled = name.isNotBlank() &&
                                    description.isNotBlank() &&
                                    !recipeViewModel.isUploading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = if (name.isNotBlank() && description.isNotBlank()) primaryGreen else Color.Gray
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Contenido principal scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .background(Color.Transparent)
            ) {
                // Selector de imagen de la receta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ImagePicker(
                        currentImageUrl = imageUrl,
                        onImageSelected = { selectedImageUri = it }
                    )
                }

                // Tarjeta principal con información básica
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Campo: Nombre de la receta
                        Text(
                            text = "Recipe Name",
                            style = MaterialTheme.typography.labelLarge,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Enter recipe name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Campo: Descripción
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.labelLarge,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Describe your recipe") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Fila con tiempo de preparación y dificultad
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Campo: Tiempo de preparación
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Prep Time",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = primaryGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = prepTime,
                                    onValueChange = { prepTime = it },
                                    placeholder = { Text("e.g., 30 min") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = primaryGreen,
                                        unfocusedBorderColor = Color.LightGray,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                )
                            }

                            // Campo: Dificultad (Dropdown)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Difficulty",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = primaryGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                var expanded by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = difficulty,
                                        onValueChange = { },
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryGreen,
                                            unfocusedBorderColor = Color.LightGray,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                                    )

                                    // Menú desplegable con opciones de dificultad
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        difficulties.forEach { diff ->
                                            DropdownMenuItem(
                                                text = { Text(text = diff) },
                                                onClick = {
                                                    difficulty = diff
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Campo: Categoría (Chips horizontales)
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelLarge,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = primaryGreen,
                                        selectedLabelColor = Color.White,
                                        containerColor = lightGray,
                                        labelColor = primaryGreen
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = Color.Transparent,
                                        selectedBorderColor = Color.Transparent,
                                        enabled = true,
                                        selected = category == cat
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta: Ingredientes
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.labelLarge,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = ingredientsText,
                            onValueChange = { ingredientsText = it },
                            placeholder = { Text("Enter ingredients, one per line") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta: Instrucciones
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.labelLarge,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = instructionsText,
                            onValueChange = { instructionsText = it },
                            placeholder = { Text("Enter instructions, one step per line") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }

                // Indicador de progreso cuando se está subiendo
                if (recipeViewModel.isUploading) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = lightGray.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = recipeViewModel.uploadProgress,
                                modifier = Modifier.fillMaxWidth(),
                                color = primaryGreen,
                                trackColor = lightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Uploading recipe...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = primaryGreen
                            )
                        }
                    }
                }

                // Mensaje de error si existe
                recipeViewModel.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Botón principal de guardar (parte inferior)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            // Lógica duplicada para botón inferior
                            val ingredients = ingredientsText
                                .split("\n")
                                .filter { it.isNotBlank() }
                                .map { it.trim() }

                            val instructions = instructionsText
                                .split("\n")
                                .filter { it.isNotBlank() }
                                .map { it.trim() }

                            val recipe = Recipe(
                                id = recipeId ?: "",
                                name = name,
                                description = description,
                                category = category,
                                ingredients = ingredients,
                                instructions = instructions,
                                imageUrl = imageUrl,
                                prepTime = prepTime,
                                difficulty = difficulty
                            )

                            if (recipeId == null) {
                                recipeViewModel.addRecipeWithImage(recipe, selectedImageUri) { success ->
                                    if (success) {
                                        navController.navigateUp()
                                    }
                                }
                            } else {
                                recipeViewModel.updateRecipeWithImage(recipe, selectedImageUri) { success ->
                                    if (success) {
                                        navController.navigateUp()
                                    }
                                }
                            }
                        },
                        enabled = name.isNotBlank() &&
                                description.isNotBlank() &&
                                !recipeViewModel.isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen,
                            disabledContainerColor = primaryGreen.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = if (recipeId == null) "Add Recipe" else "Save Changes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}