package com.example.chefai.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Import Material 3 components
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel composable
import com.example.chefai.model.Recipe // Import your Recipe model
import com.example.chefai.ui.theme.components.BottomNavBar
import com.example.chefai.ui.theme.nav.Screen
import com.example.chefai.viewmodel.SearchViewModel // Import the new SearchViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.navigation.NavHostController // Import NavHostController if navigating from here

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    // Add NavController if you want to navigate from search results to RecipeCardScreen
    navController: NavHostController, // Pass NavController
    onNavigate: (Screen) -> Unit,
    searchViewModel: SearchViewModel = viewModel() // Get instance of SearchViewModel
) {
    // Observe the search query state from the ViewModel
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    // Observe the list of recipes state from the ViewModel
    val recipes by searchViewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search Saved Recipes") })
        },
        bottomBar = { BottomNavBar(onNavigate) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Consistent padding
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchViewModel.onSearchQueryChanged(it) }, // Update ViewModel on change
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("Search by Title") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                singleLine = true
            )

            // List of Saved Recipes
            if (recipes.isEmpty() && searchQuery.isBlank()) {
                // Show a message if the database is empty initially
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No recipes saved yet.")
                }
            } else if (recipes.isEmpty() && searchQuery.isNotBlank()) {
                // Show a message if search yields no results
                Box(modifier = Modifier.fillMaxSize().padding(top = 30.dp), contentAlignment = Alignment.TopCenter) {
                    Text("No recipes found matching \"$searchQuery\".")
                }
            }
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Space between recipe cards
                ) {
                    items(recipes, key = { it.id }) { recipe -> // Use recipe ID as key
                        RecipeListItem(recipe = recipe) {
                            // --- TODO: Handle navigation to detail screen ---
                            // Navigate to RecipeCardScreen when a card is clicked
                            // Re-use the navigation argument logic from HomeScreen
                            // Need to encode recipe details again here
                            val gson = Gson()
                            val ingredientsJson = gson.toJson(recipe.ingredients)
                            val instructionsJson = gson.toJson(recipe.instructions)
                            fun encode(value: String?): String { return URLEncoder.encode(value ?: "null", StandardCharsets.UTF_8.toString()) }

                            val route = Screen.Recipe.buildRoute(
                                title = encode(recipe.title), description = encode(recipe.description),
                                prepTime = encode(recipe.prepTime), cookTime = encode(recipe.cookTime),
                                totalTime = encode(recipe.totalTime), servings = encode(recipe.servings),
                                ingredientsJson = encode(ingredientsJson), instructionsJson = encode(instructionsJson),
                                imagePrompt = encode(recipe.imagePrompt), imageUrl = encode(recipe.imageUrl)
                            )
                            navController.navigate(route)
                        }
                    }
                }
            }
        }
    }
}

// Simple Composable for displaying one recipe item in the list
@Composable
fun RecipeListItem(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Make card clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optional: Add a small placeholder/icon here if needed
            // AsyncImage(...)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recipe.description.ifBlank { "No description available." },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}