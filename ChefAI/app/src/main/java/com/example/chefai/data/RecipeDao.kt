package com.example.chefai.data // Or your chosen package (e.g., com.example.chefai.db)

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chefai.model.Recipe // Import your Recipe entity
import kotlinx.coroutines.flow.Flow // Import Flow for reactive queries

/**
 * Data Access Object (DAO) for the Recipe entity.
 * Defines methods for interacting with the recipes table in the database.
 */
@Dao // Mark this as a Data Access Object
interface RecipeDao {

    /**
     * Inserts a recipe into the database. If a recipe with the same ID already
     * exists, it replaces the old entry.
     * 'suspend' indicates this should be called from a coroutine or another suspend function.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    /**
     * Retrieves all saved recipes from the database, ordered by ID descending (newest first).
     * Returns a Flow, which allows the UI to automatically update when the data changes.
     */
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    /**
     * Retrieves all saved recipes that contain the search query in their title,
     * ordered by ID descending. Uses '%' wildcards for partial matching.
     * Returns a Flow for automatic UI updates.
     */
    @Query("SELECT * FROM recipes WHERE recipe_title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchRecipes(query: String): Flow<List<Recipe>>

    // Add other functions later (e.g., delete, update, get by ID) as needed

}