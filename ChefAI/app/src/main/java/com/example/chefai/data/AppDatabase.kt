package com.example.chefai.data // Or your chosen package

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chefai.model.Recipe // Import your Recipe entity
import com.example.chefai.model.StringListConverter // Import your TypeConverter class

/**
 * The main Room database class for the application.
 * This class defines the database configuration and serves as the main access point
 * to the underlying connection and the DAOs.
 */
// List all entities (tables) associated with the database in the 'entities' array.
// Version number must be incremented if you change the schema.
@Database(entities = [Recipe::class], version = 1, exportSchema = false)
// Register the TypeConverters needed for this database (e.g., for List<String>).
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // Abstract function for Room to generate the implementation of your DAO.
    abstract fun recipeDao(): RecipeDao

    // Companion object to provide a singleton instance of the database.
    // This prevents having multiple instances of the database opened at the same time.
    companion object {
        // @Volatile annotation ensures that the value of INSTANCE is always up-to-date
        // and visible to all threads immediately.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Function to get the singleton database instance.
        fun getDatabase(context: Context): AppDatabase {
            // If INSTANCE is not null, return it; otherwise, create the database.
            return INSTANCE ?: synchronized(this) { // synchronized protects from concurrent creation
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context
                    AppDatabase::class.java,
                    "chefai_database"          // Name of the database file
                )
                    // In a production app, add migration strategies here if schema changes.
                    // .addMigrations(...)
                    .build()
                INSTANCE = instance
                // Return the newly created instance
                instance
            }
        }
    }
}