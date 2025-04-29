package com.example.chefai.viewmodel

// Import Application and AndroidViewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel // Import AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefai.BuildConfig
import com.example.chefai.data.AppDatabase // Import AppDatabase
import com.example.chefai.data.RecipeDao // Import RecipeDao
import com.example.chefai.model.Recipe
import com.example.chefai.network.ChatMessage
import com.example.chefai.network.ChatRequest
import com.example.chefai.network.DallERequest
import com.example.chefai.network.RecipeApiService
import com.example.chefai.network.RetrofitInstance
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RecipeUiState {
    object Initial : RecipeUiState
    object Loading : RecipeUiState
    data class Success(val recipe: Recipe) : RecipeUiState
    data class Error(val message: String) : RecipeUiState
}

// Change ViewModel to AndroidViewModel to get Application context
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState: MutableStateFlow<RecipeUiState> =
        MutableStateFlow(RecipeUiState.Initial)
    val uiState: StateFlow<RecipeUiState> =
        _uiState.asStateFlow()

    private val apiService: RecipeApiService = RetrofitInstance.api
    private val gson = Gson() // Keep Gson instance if using JSON parsing

    // --- Get instance of the DAO ---
    private val recipeDao: RecipeDao

    init {
        // Get database instance and DAO in the init block
        val database = AppDatabase.getDatabase(application)
        recipeDao = database.recipeDao()
    }
    // --- End DAO instance ---

    /**
     * Generates recipe text, optionally an image, and saves the result to Room DB.
     */
    fun generateRecipe(idea: String, ingredients: String, name: String?) {
        _uiState.value = RecipeUiState.Loading
        viewModelScope.launch {
            var parsedRecipe: Recipe? = null // Renamed from recipeFromJson for clarity
            var imageUrl: String? = null

            try {
                // --- Step 1 & 2: Generate and Parse Recipe Text ---
                // Assuming JSON parsing is intended based on last code provided
                val systemPrompt = createSystemPrompt() // Should ask for JSON
                val userPrompt = createUserPrompt(idea, ingredients, name)
                val messages = listOf(ChatMessage("system", systemPrompt), ChatMessage("user", userPrompt))
                val chatRequest = ChatRequest(model = "gpt-4o-mini", messages = messages)
                val apiKeyHeader = "Bearer ${BuildConfig.OPENAI_API_KEY}"
                val chatResponse = apiService.generateChatCompletion(apiKeyHeader, chatRequest)

                if (chatResponse.isSuccessful && chatResponse.body() != null) {
                    val assistantMessageContent = chatResponse.body()?.choices?.firstOrNull()?.message?.content
                    println("Raw OpenAI JSON Response Content:\n$assistantMessageContent")
                    if (!assistantMessageContent.isNullOrBlank()) {
                        parsedRecipe = parseRecipeResponse(assistantMessageContent) // Use JSON parser
                        if (parsedRecipe == null) {
                            _uiState.value = RecipeUiState.Error("Failed to parse JSON recipe from OpenAI response.")
                            return@launch
                        }
                    } else { _uiState.value = RecipeUiState.Error("OpenAI recipe JSON content was empty."); return@launch }
                } else { val errorBody = chatResponse.errorBody()?.string() ?: "Unknown API error (Chat)"; _uiState.value = RecipeUiState.Error("API Error ${chatResponse.code()}: Check logs (Chat)."); return@launch }


                // --- Step 3: Generate Image (Optional) ---
                if (parsedRecipe != null && parsedRecipe.imagePrompt.isNotBlank()) {
                    println("Attempting to generate image with prompt: ${parsedRecipe.imagePrompt}")
                    try {
                        // Create request WITHOUT quality for dall-e-2
                        val imageRequest = DallERequest(model = "dall-e-2", prompt = parsedRecipe.imagePrompt)
                        val imageResponse = apiService.generateImage(apiKeyHeader, imageRequest)
                        if (imageResponse.isSuccessful && imageResponse.body() != null) {
                            imageUrl = imageResponse.body()?.data?.firstOrNull()?.url
                            println("Image URL received: $imageUrl")
                            if(imageUrl.isNullOrBlank()) { println("Warning: Image URL from DALL-E was null or blank.") }
                        } else { val errorBody = imageResponse.errorBody()?.string() ?: "Unknown API error (Image)"; println("OpenAI DALL-E API Error: ${imageResponse.code()} - $errorBody") }
                    } catch (imgEx: Exception) { println("Error during DALL-E API call: ${imgEx.message}"); imgEx.printStackTrace() }
                } else { println("Skipping image generation.") }

                // --- Step 4: Create Final Recipe and Insert into Database ---
                val finalRecipe = parsedRecipe?.copy(imageUrl = imageUrl) // Combine text recipe with image URL

                if (finalRecipe != null) {
                    // --- INSERT into Room Database ---
                    try {
                        // Use viewModelScope for database operations too
                        // No need for separate scope if already inside one
                        recipeDao.insertRecipe(finalRecipe) // Call DAO's insert function
                        println("Recipe saved to database: ${finalRecipe.title}") // Log success
                        // Set Success State for UI AFTER saving
                        _uiState.value = RecipeUiState.Success(finalRecipe)
                    } catch(dbEx: Exception) {
                        println("Error saving recipe to database: ${dbEx.message}")
                        dbEx.printStackTrace()
                        // Still consider the generation a success for the UI, but log DB error
                        // Or set a specific state/message indicating save failed
                        _uiState.value = RecipeUiState.Success(finalRecipe) // Show recipe even if save failed
                        // Optionally: _uiState.value = RecipeUiState.Error("Recipe generated but failed to save.")
                    }
                    // --- End Insert ---
                } else {
                    _uiState.value = RecipeUiState.Error("Failed to finalize recipe object.")
                }

            } catch (e: Exception) { // Catch errors from chat completion or other steps
                println("Error during recipe generation process: ${e.message}")
                e.printStackTrace()
                _uiState.value = RecipeUiState.Error(e.localizedMessage ?: "An overall error occurred")
            }
        }
    }

    // System prompt asking for JSON
    private fun createSystemPrompt(): String {
        return """
        You are ChefAI, an expert culinary assistant. Your task is to generate a recipe based on user-provided ideas and ingredients.
       You MUST provide the response strictly in the following structured format, using these exact headings and markdown:


       **Title:** [Generated Recipe Title]
      
       **Description:** [A short, appealing description of the dish, 1-2 sentences]
      
       **Prep Time:** [Estimated preparation time, e.g., 15 minutes]
      
       **Cook Time:** [Estimated cooking time, e.g., 30 minutes]
      
       **Total Time:** [Estimated total time]
      
       **Servings:** [Number of servings, e.g., 4 servings]
      
       **Ingredients:**
       - [Quantity] [Unit] [Ingredient Name]
       - [Quantity] [Unit] [Ingredient Name]
       (List all necessary ingredients)
      
       **Instructions:**
       1. [Step 1 description]
       2. [Step 2 description]
       (List all steps clearly and concisely)
      
       **Image Prompt:** [A detailed text description suitable for an AI image generator to create a picture of the final dish. Example: 'A vibrant photo of freshly cooked pasta aglio e olio in a white bowl, garnished with parsley, close-up shot, natural light.']


       Do not include any extra introductory or concluding text outside of this structure. Ensure each section is clearly marked with the headings in bold. Use bullet points (-) for ingredients and numbered lists (1., 2.) for instructions.

        """.trimIndent() // Ensure you have the full JSON prompt here
    }

    // User prompt remains the same
    private fun createUserPrompt(idea: String, ingredients: String, name: String?): String {
        // ... (keep exact same user prompt) ...
        return """
       Generate a recipe based on the following details:
       Recipe Idea: "$idea"
       Ingredients Available: "$ingredients"
       ${if (!name.isNullOrBlank()) "Optional Recipe Name Suggestion: \"$name\"" else ""}
       """.trimIndent()
    }



    private fun parseRecipeResponse(responseText: String?): Recipe? {
        // --- Keep the exact same parsing logic here that uses extractSection ---
        // --- Make sure it returns a Recipe object with all text fields populated ---
        if (responseText.isNullOrBlank()) { /*...*/ return null }
        println("--- Attempting to Parse Response ---")
        println(responseText)
        println("------------------------------------")
        try {
            fun extractSection(startTag: String, endTag: String): String {
                val contentAfterStart = responseText.substringAfter(startTag, "")
                val endIndex = contentAfterStart.indexOf(endTag)
                return if (endIndex != -1) {
                    contentAfterStart.substring(0, endIndex).trim()
                } else {
                    contentAfterStart.trim()
                }
            }
            val title = extractSection("**Title:**", "\n").ifBlank { "Generated Recipe" }
            val description = extractSection("**Description:**", "\n")
            val prepTime = extractSection("**Prep Time:**", "\n")
            val cookTime = extractSection("**Cook Time:**", "\n")
            val totalTime = extractSection("**Total Time:**", "\n")
            val servings = extractSection("**Servings:**", "\n")
            val ingredientsBlock = extractSection("**Ingredients:**", "**Instructions:**")
            val instructionsBlock = extractSection("**Instructions:**", "**Image Prompt:**")
            val imagePrompt = responseText.substringAfter("**Image Prompt:**", "").trim()


            val ingredientsList = ingredientsBlock.lines()
                // This part processes each line to create the ingredients list
                .mapNotNull { line ->
                    val processedLine = line.removePrefix("- ").trim()
                    processedLine.takeIf { it.isNotEmpty() } // This should return String?
                }


            val instructionsList = instructionsBlock.lines()
                // This part processes each line to create the instructions list
                .mapNotNull { line ->
                    val processedLine = line.replaceFirst(Regex("^\\d+\\.?\\s*"), "").trim()
                    processedLine.takeIf { it.isNotEmpty() } // This should return String?
                }




            // IMPORTANT: Create Recipe object here but imageUrl is initially null
            val parsedRecipeObject = Recipe(
                title = title, description = description, prepTime = prepTime, cookTime = cookTime,
                totalTime = totalTime, servings = servings, ingredients = ingredientsList,
                instructions = instructionsList, imagePrompt = imagePrompt, imageUrl = null // Initialize imageUrl as null here
            )


            if (parsedRecipeObject.title == "Generated Recipe" && parsedRecipeObject.ingredients.isEmpty() && parsedRecipeObject.instructions.isEmpty() && parsedRecipeObject.imagePrompt.isBlank()) {
                println("Parsing Warning: Failed to extract any core recipe details.")
                return null
            }
            println("--- Parsed Text Data ---") // Log parsed text data
            println("Title: $title")
            // ... log other fields ...
            println("------------------------")
            return parsedRecipeObject // Return the parsed object (without image URL yet)


        } catch (e: Exception) {
            println("Error parsing recipe response: ${e.message}")
            e.printStackTrace()
            return null
        }
    }


    fun resetState() {
        _uiState.value = RecipeUiState.Initial
    }
}
