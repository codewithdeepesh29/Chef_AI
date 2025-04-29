package com.example.chefai.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Import Chat models (already there)
import com.example.chefai.network.ChatRequest
import com.example.chefai.network.ChatResponse

// --- Import DALL-E models ---
import com.example.chefai.network.DallERequest
import com.example.chefai.network.DallEResponse

interface RecipeApiService {

    /**
     * Defines the network call to OpenAI's Chat Completions endpoint (for recipe text).
     */
    @POST("v1/chat/completions")
    suspend fun generateChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    /**
     * Defines the network call to OpenAI's Image Generation endpoint (DALL-E).
     */
    // --- ADD THIS NEW FUNCTION ---
    @POST("v1/images/generations") // Correct endpoint for DALL-E
    suspend fun generateImage(
        @Header("Authorization") apiKey: String, // Use the same API key header
        @Body request: DallERequest // Use the DallERequest model for the body
    ): Response<DallEResponse> // Expect a DallEResponse back
    // --- END OF NEW FUNCTION ---

}