package com.example.chefai.ui.theme.components


import androidx.compose.foundation.layout.Spacer
//import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.chefai.ui.theme.nav.Screen

@Composable
fun BottomNavBar(onNavigate: (Screen) -> Unit) {
    BottomAppBar(containerColor = Color(0xFFD7E8D1)) {
        IconButton(onClick = { onNavigate(Screen.Recipe) }) {
            Icon(Icons.Default.List, contentDescription = "Recipe")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        IconButton(onClick = { onNavigate(Screen.Home) }) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        IconButton(onClick = { onNavigate(Screen.Search) }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}
