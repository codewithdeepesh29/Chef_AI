package com.example.chefai.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chefai.ui.theme.components.BottomNavBar
import com.example.chefai.ui.theme.nav.Screen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import coil.compose.AsyncImage // Keep Coil import
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.chefai.R // Ensure this matches your project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCardScreen(
    // Accept all parameters, including the new imageUrl
    title: String,
    description: String,
    prepTime: String,
    cookTime: String,
    totalTime: String,
    servings: String,
    ingredients: List<String>,
    instructions: List<String>,
    imagePrompt: String,
    imageUrl: String?, // <-- ACCEPT imageUrl parameter here (nullable)
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = title, maxLines = 1) })
        },
        bottomBar = { BottomNavBar(selectedScreen = Screen.Recipe, onNavigate = onNavigate) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Use the actual imageUrl in AsyncImage ---
            AsyncImage(
                model = imageUrl, // <-- USE the passed imageUrl here
                contentDescription = title,
                placeholder = painterResource(id = R.drawable.chefai_logo), // Fallback if imageUrl is null or loading
                error = painterResource(id = R.drawable.chefai_logo), // Fallback on error
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f) // Set aspect ratio
                    .clip(RoundedCornerShape(12.dp)), // Apply rounded corners
                contentScale = ContentScale.Crop // Crop to fill bounds
            )
            // --- End AsyncImage ---

            // Description
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                HorizontalDivider()
            }

            // Details Card
            Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            if (prepTime.isNotBlank()) Text("Prep: $prepTime", style = MaterialTheme.typography.bodyMedium)
                            if (cookTime.isNotBlank()) Text("Cook: $cookTime", style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (totalTime.isNotBlank()) Text("Total: $totalTime", style = MaterialTheme.typography.bodyMedium)
                            if (servings.isNotBlank()) Text("Servings: $servings", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Ingredients Card
            Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("Ingredients", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    ingredients.forEach { ingredient ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("• ", style = MaterialTheme.typography.bodyLarge)
                            Text(ingredient, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Instructions Card
            Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("Instructions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    instructions.forEachIndexed { index, instruction ->
                        Row(modifier = Modifier.padding(bottom = 4.dp)) {
                            Text("${index + 1}. ", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(instruction, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // (Optional) Image Prompt Card
            if (imagePrompt.isNotBlank()) {
                Card(elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("Image Prompt (for AI)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(imagePrompt, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } // End Column
    } // End Scaffold
} // End RecipeCardScreen