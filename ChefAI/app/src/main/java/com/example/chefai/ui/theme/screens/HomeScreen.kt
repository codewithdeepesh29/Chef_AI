@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chefai.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chefai.R
import com.example.chefai.ui.theme.components.BottomNavBar
import com.example.chefai.ui.theme.nav.Screen // Keep this for BottomNavBar if needed
import com.example.chefai.viewmodel.RecipeViewModel
import com.example.chefai.viewmodel.RecipeUiState // Ensure this import is correct
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.navigation.NavHostController // Ensure this import is present

@Composable
fun HomeScreen(
    navController: NavHostController,
    onNavigate: (Screen) -> Unit,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    var idea by rememberSaveable { mutableStateOf("") }
    var ingredients by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val recipeState by recipeViewModel.uiState.collectAsState()

    LaunchedEffect(recipeState) {
        when (val state = recipeState) {
            is RecipeUiState.Success -> { // Check 'Success' - error "Unresolved ref 'Success'" means RecipeUiState import/def issue
                Toast.makeText(context, "Recipe Generated! Navigating...", Toast.LENGTH_SHORT).show()
                val recipe = state.recipe // Check 'recipe' - error "Unresolved ref 'recipe'" means RecipeUiState import/def issue
                val gson = Gson()
                val ingredientsJson = gson.toJson(recipe.ingredients)
                val instructionsJson = gson.toJson(recipe.instructions)

                // Helper function for URL encoding
                fun encode(value: String?): String {
                    // Handle null by encoding a specific string like "null" or an empty string
                    return URLEncoder.encode(value ?: "null", StandardCharsets.UTF_8.toString())
                }

                // URL-encode arguments, including the nullable imageUrl
                val titleEncoded = encode(recipe.title)
                val descriptionEncoded = encode(recipe.description)
                val prepTimeEncoded = encode(recipe.prepTime)
                val cookTimeEncoded = encode(recipe.cookTime)
                val totalTimeEncoded = encode(recipe.totalTime)
                val servingsEncoded = encode(recipe.servings)
                val ingredientsEncoded = encode(ingredientsJson)
                val instructionsEncoded = encode(instructionsJson)
                val imagePromptEncoded = encode(recipe.imagePrompt)
                val imageUrlEncoded = encode(recipe.imageUrl) // <-- Get & Encode imageUrl from state

                // Construct the route using the updated buildRoute from Navigation.kt
                val route = Screen.Recipe.buildRoute(
                    title = titleEncoded, description = descriptionEncoded, prepTime = prepTimeEncoded,
                    cookTime = cookTimeEncoded, totalTime = totalTimeEncoded, servings = servingsEncoded,
                    ingredientsJson = ingredientsEncoded, instructionsJson = instructionsEncoded,
                    imagePrompt = imagePromptEncoded,
                    imageUrl = imageUrlEncoded // <-- PASS the encoded imageUrl here
                    // Error "No value passed for parameter 'imageUrl'" happens if this argument is missing ^
                )
                println("Navigating with route: $route")

                // Perform navigation using ONLY the correct route
                navController.navigate(route)

                recipeViewModel.resetState()
            }
            is RecipeUiState.Error -> { // Check 'Error' - error "Unresolved ref 'Error'" means RecipeUiState import/def issue
                // Check 'message' - error "Unresolved ref 'message'" means RecipeUiState import/def issue
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                // Error on previous line about makeText or show() might be side effect of state.message being unresolved
                recipeViewModel.resetState()
            }
            is RecipeUiState.Loading -> { /* Handled below by enabled/visibility checks */ }
            RecipeUiState.Initial -> { /* Initial state, do nothing specific here */ }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(onNavigate) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.chefai_logo),
                contentDescription = "ChefAI Logo",
                modifier = Modifier.fillMaxWidth(0.8f).height(120.dp)
            )
            Text(
                text = "ChefAI",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Black
            )
            // --- Input Cards ---
            Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recipe Idea", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(value = idea, onValueChange = { idea = it }, placeholder = { Text("E.g. Quick weekday dinner, Italian Pasta") }, modifier = Modifier.fillMaxWidth().background(Color(0xFFF0FFF0), shape = MaterialTheme.shapes.medium), singleLine = true)
                }
            }
            Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ingredients Available", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(value = ingredients, onValueChange = { ingredients = it }, placeholder = { Text("E.g. Chicken breast, tomatoes, basil, olive oil") }, modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFF0FFF0), shape = MaterialTheme.shapes.medium))
                }
            }
            Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recipe Name (Optional)", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(value = name, onValueChange = { name = it }, placeholder = { Text("E.g. My Awesome Chicken Dish") }, modifier = Modifier.fillMaxWidth().background(Color(0xFFF0FFF0), shape = MaterialTheme.shapes.medium), singleLine = true)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            // --- Generate Button ---
            Button(
                onClick = {
                    if (idea.isBlank() || ingredients.isBlank()) {
                        Toast.makeText(context, "Please fill Recipe Idea and Ingredients", Toast.LENGTH_SHORT).show()
                    } else {
                        recipeViewModel.generateRecipe(idea, ingredients, name.takeIf { it.isNotBlank() })
                    }
                },
                // Error "Unresolved ref 'Loading'" here means RecipeUiState import/def issue
                enabled = recipeState !is RecipeUiState.Loading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Generate Recipe")
            }
            // --- Loading Indicator ---
            // Error "Unresolved ref 'Loading'" here means RecipeUiState import/def issue
            if (recipeState is RecipeUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Spacer(modifier = Modifier.height(32.dp))
        } // End Column
    } // End Scaffold
} // End HomeScreen