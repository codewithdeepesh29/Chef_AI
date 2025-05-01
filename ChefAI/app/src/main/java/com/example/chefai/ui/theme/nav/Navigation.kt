package com.example.chefai.ui.theme.nav

/* ───────────────────────── imports ───────────────────────── */
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chefai.ui.theme.screens.HomeScreen
import com.example.chefai.ui.theme.screens.RecipeCardScreen
import com.example.chefai.ui.theme.screens.SearchScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/* ─────────────────── sealed class for screens ─────────────────── */
sealed class Screen(val routeBase: String) {

    /* Home */
    object Home : Screen("home")

    /* Recipe detail */
    object Recipe : Screen("recipe") {

        // -- argument keys
        const val ARG_TITLE       = "title"
        const val ARG_DESC        = "description"
        const val ARG_PREP        = "prepTime"
        const val ARG_COOK        = "cookTime"
        const val ARG_TOTAL       = "totalTime"
        const val ARG_SERV        = "servings"
        const val ARG_INGR        = "ingredientsJson"
        const val ARG_INSTR       = "instructionsJson"
        const val ARG_IMG_PROMPT  = "imagePrompt"
        const val ARG_IMG_URL     = "imageUrl"

        /* nav-graph pattern  ( {placeholder} for every arg ) */
        val routePattern =
            "$routeBase/{$ARG_TITLE}/{$ARG_DESC}/{$ARG_PREP}/{$ARG_COOK}/" +
                    "{$ARG_TOTAL}/{$ARG_SERV}/{$ARG_INGR}/{$ARG_INSTR}/{$ARG_IMG_PROMPT}/{$ARG_IMG_URL}"

        /* helper used by HomeScreen when it builds the route */
        fun buildRoute(
            title: String,
            description: String,
            prepTime: String,
            cookTime: String,
            totalTime: String,
            servings: String,
            ingredientsJson: String,
            instructionsJson: String,
            imagePrompt: String,
            imageUrl: String            // already URL-encoded (use "null" for real nulls)
        ) = "$routeBase/$title/$description/$prepTime/$cookTime/" +
                "$totalTime/$servings/$ingredientsJson/$instructionsJson/$imagePrompt/$imageUrl"
    }

    /* Search */
    object Search : Screen("search")
}

/* ──────────────────────── NavHost ──────────────────────── */
@Composable
fun RecipeApp(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.routeBase
    ) {

        /* ---------- Home ---------- */
        composable(Screen.Home.routeBase) {
            HomeScreen(
                navController = navController,
                onNavigate   = { screen -> navController.navigate(screen.routeBase)},
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        /* ---------- Recipe detail ---------- */
        composable(
            route = Screen.Recipe.routePattern,
            arguments = listOf(
                navArgument(Screen.Recipe.ARG_TITLE      ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_DESC       ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_PREP       ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_COOK       ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_TOTAL      ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_SERV       ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_INGR       ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_INSTR      ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_IMG_PROMPT ) { type = NavType.StringType },
                navArgument(Screen.Recipe.ARG_IMG_URL    ) {
                    type = NavType.StringType
                    nullable = true
                },
            )
        ) { backStackEntry ->

            /* ▼ helpers to decode the URL-encoded args */
            fun decodeArg(key: String, fallback: String = ""): String =
                URLDecoder.decode(
                    backStackEntry.arguments?.getString(key) ?: fallback,
                    StandardCharsets.UTF_8
                )

            fun decodeNullableArg(key: String): String? =
                backStackEntry.arguments?.getString(key)
                    ?.takeIf { it.lowercase() != "null" }
                    ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }

            /* ▼ read & transform parameters */
            val title            = decodeArg(Screen.Recipe.ARG_TITLE)
            val description      = decodeArg(Screen.Recipe.ARG_DESC)
            val prepTime         = decodeArg(Screen.Recipe.ARG_PREP)
            val cookTime         = decodeArg(Screen.Recipe.ARG_COOK)
            val totalTime        = decodeArg(Screen.Recipe.ARG_TOTAL)
            val servings         = decodeArg(Screen.Recipe.ARG_SERV)
            val ingredientsJson  = decodeArg(Screen.Recipe.ARG_INGR)
            val instructionsJson = decodeArg(Screen.Recipe.ARG_INSTR)
            val imagePrompt      = decodeArg(Screen.Recipe.ARG_IMG_PROMPT)
            val imageUrl         = decodeNullableArg(Screen.Recipe.ARG_IMG_URL)

            /* convert the two JSON strings back to lists */
            val gson            = Gson()
            val listType        = object : TypeToken<List<String>>() {}.type
            val ingredientsList = runCatching { gson.fromJson<List<String>>(ingredientsJson, listType) }
                .getOrDefault(emptyList())
            val instructionsList = runCatching { gson.fromJson<List<String>>(instructionsJson, listType) }
                .getOrDefault(emptyList())

            /* ---------- screen ---------- */
            RecipeCardScreen(
                title         = title,
                description   = description,
                prepTime      = prepTime,
                cookTime      = cookTime,
                totalTime     = totalTime,
                servings      = servings,
                ingredients   = ingredientsList,
                instructions  = instructionsList,
                imagePrompt   = imagePrompt,
                imageUrl      = imageUrl,
                onNavigate    = { screen -> navController.navigate(screen.routeBase) }
            )
        }

        /* ---------- Search ---------- */
        composable(Screen.Search.routeBase) {
            SearchScreen(
                navController = navController,             // ← missing param fixed
                onNavigate   = { screen -> navController.navigate(screen.routeBase) }
            )
        }
    }
}
