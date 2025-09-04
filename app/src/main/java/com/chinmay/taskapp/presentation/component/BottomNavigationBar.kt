package com.chinmay.taskapp.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(selectedScreen: String, onScreenSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color(0xFFF8EDEB)) { // Soft pastel background

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedScreen == "Home",
            onClick = { onScreenSelected("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF9A8C98), // Muted lavender
                unselectedIconColor = Color(0xFFC9ADA7) // Soft warm gray
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.HourglassEmpty, contentDescription = "Pending Tasks") },
            label = { Text("Pending") },
            selected = selectedScreen == "Pending",
            onClick = { onScreenSelected("Pending") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF9A8C98),
                unselectedIconColor = Color(0xFFC9ADA7)
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.TaskAlt, contentDescription = "Completed Tasks") },
            label = { Text("Completed") },
            selected = selectedScreen == "Completed",
            onClick = { onScreenSelected("Completed") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF9A8C98),
                unselectedIconColor = Color(0xFFC9ADA7)
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "All Tasks") },
            label = { Text("All") },
            selected = selectedScreen == "All",
            onClick = { onScreenSelected("All") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF9A8C98),
                unselectedIconColor = Color(0xFFC9ADA7)
            )
        )
    }
}
