import java.util.Properties // <-- Import needed for Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" // Ensure prefix matches Kotlin version
}

android {
    namespace = "com.example.chefai"
    compileSdk = 35 // Using 35, ensure you have the corresponding SDK installed

    defaultConfig {
        applicationId = "com.example.chefai"
        minSdk = 24
        targetSdk = 35 // Using 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        // --- THIS IS THE buildConfigField LINE ---
        buildConfigField("String", "OPENAI_API_KEY", "\"${localProperties.getProperty("OPENAI_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true // <-- Enable BuildConfig generation
    }
    // If you are using compose compiler plugin explicitly
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "YOUR_COMPOSE_COMPILER_VERSION" // Make sure this is set if needed
    // }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // Use the Compose BOM
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Use Material 3 from BOM

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.0") // Update to match lifecycle versions if possible, check latest stable
    implementation("androidx.compose.material:material-icons-extended") // No need for version if using BOM



    // ViewModel and Lifecycle Compose integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // --- Networking (You have duplicates, clean up if desired) ---
    // Retrofit (Keep one set)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp Logging Interceptor (Keep one)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0") // Keep the newer one

    implementation("io.coil-kt:coil-compose:2.6.0")

    // Room Persistence Library
    val room_version = "2.6.1" // Use the latest stable version
    implementation("androidx.room:room-runtime:$room_version")
    // Optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    // KSP Annotation Processor for Room
    ksp("androidx.room:room-compiler:$room_version")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Use BOM for test dependencies too
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}