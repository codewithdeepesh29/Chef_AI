package com.example.chefai.network

// Import BuildConfig if you were planning to use the Interceptor method for auth later
import com.example.chefai.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // --- CHANGE Base URL to OpenAI API ---
    private const val BASE_URL = "https://api.openai.com/" // <-- Updated This Line

    // Logging interceptor remains useful for debugging
    // Ensure your gradle file includes the dependency:
    // implementation("com.squareup.okhttp3:logging-interceptor:4.10.0") // Or newer
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Log request and response bodies. Be careful in production if sensitive data is logged.
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient with logging enabled.
    // No changes needed here if passing Auth header via the ApiService interface.
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Create and configure the Retrofit instance using the updated BASE_URL
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // <-- Uses the updated BASE_URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Expose the API service (which is now the updated RecipeApiService interface)
    val api: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }
}