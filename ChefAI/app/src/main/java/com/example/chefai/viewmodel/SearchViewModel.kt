package com.example.chefai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefai.data.AppDatabase
import com.example.chefai.data.RecipeDao
import com.example.chefai.model.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.* // Import necessary Flow operators

/**
 * ViewModel for the SearchScreen.
 * Handles fetching and searching saved recipes from the Room database.
 */
// Opt-in for experimental Flow APIs like stateIn if needed by your specific versions
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeDao: RecipeDao

    // StateFlow for the current search query (we'll use this later)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow for the list of recipes to display
    // It observes changes based on the search query
    val recipes: StateFlow<List<Recipe>>

    init {
        val database = AppDatabase.getDatabase(application)
        recipeDao = database.recipeDao()

        // Initialize the recipes StateFlow
        // flatMapLatest ensures that when the searchQuery changes,
        // the previous database query is cancelled and a new one starts.
        recipes = searchQuery
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    recipeDao.getAllRecipes() // Get all if query is blank
                } else {
                    recipeDao.searchRecipes(query) // Search if query is not blank
                }
            }
            // Convert the Flow to a StateFlow to be observed by the UI
            // SharingStarted.WhileSubscribed stops the flow when the UI is not observing
            // initialValue sets an empty list while the first query runs
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Keep Flow active 5s after UI stops observing
                initialValue = emptyList() // Start with an empty list
            )
    }

    /**
     * Updates the search query. Called by the UI when the search text changes.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

}