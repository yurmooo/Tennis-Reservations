package com.example.tennisapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.tennisapp.BottomNavItem
import com.example.tennisapp.roboto

@Composable
fun BottomBar(
    navController: NavController,
    currentDestination: NavDestination?
) {
    val items = listOf (
        BottomNavItem (
            route = "main_screen",
            title = "Главная",
            icon = Icons.Default.Home
        ),
        BottomNavItem (
            route = "booking_screen",
            title = "Бронирование",
            icon = Icons.Default.DateRange
        ),
        BottomNavItem (
            route = "profile_screen",
            title = "Профиль",
            icon = Icons.Default.AccountCircle
        )
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.route == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon (
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title, fontFamily = roboto)
                }
            )
        }
    }
}