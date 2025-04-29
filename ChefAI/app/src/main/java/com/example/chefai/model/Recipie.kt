package com.example.chefai.model

import androidx.room.ColumnInfo // Import Room annotations
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson // For JSON conversion
import com.google.gson.reflect.TypeToken

// Define Type Converters for List<String>
class StringListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { gson.toJson(it) } // Convert list to JSON string
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            try {
                gson.fromJson(it, listType) // Convert JSON string back to list
            } catch (e: Exception) {
                // Handle potential parsing errors, maybe return empty list
                println("Error converting JSON to List<String>: ${e.message}")
                emptyList()
            }
        } ?: emptyList() // Return empty list if JSON is null
    }
}


// Annotate the data class as an Entity
@Entity(tableName = "recipes") // Specify table name
@TypeConverters(StringListConverter::class) // Tell Room to use the converter
data class Recipe(
    // Add a primary key - autoGenerate ensures uniqueness
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Use Int or Long for auto-generated ID, default 0

    @ColumnInfo(name = "recipe_title") // Optional: specify column name
    val title: String = "Unknown Recipe",

    val description: String = "", // Room uses field name as column name by default
    val prepTime: String = "",
    val cookTime: String = "",
    val totalTime: String = "",
    val servings: String = "",

    // Room will use StringListConverter to store/retrieve these
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),

    val imagePrompt: String = "",
    val imageUrl: String? = null
)