package com.example.chefai.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chefai.ui.theme.nav.Screen

@Composable
fun BottomNavBar(
    selectedScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavBarItem(
                screen = Screen.Recipe,
                icon = Icons.AutoMirrored.Filled.List,
                selected = selectedScreen == Screen.Recipe,
                onClick = { onNavigate(Screen.Recipe) }
            )
            NavBarItem(
                screen = Screen.Home,
                icon = Icons.Filled.Home,
                selected = selectedScreen == Screen.Home,
                onClick = { onNavigate(Screen.Home) }
            )
            NavBarItem(
                screen = Screen.Search,
                icon = Icons.Filled.Search,
                selected = selectedScreen == Screen.Search,
                onClick = { onNavigate(Screen.Search) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    screen: Screen,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = screen.routeBase,
            tint = color
        )
    }
}
