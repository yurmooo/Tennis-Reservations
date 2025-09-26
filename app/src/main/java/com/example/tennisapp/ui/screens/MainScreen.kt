package com.example.tennisapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tennisapp.ui.components.AppBar
import com.example.tennisapp.MainContent
import com.example.tennisapp.database.authorizeUser
import com.example.tennisapp.ui.components.BottomBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold (
        topBar = {
            if (currentDestination?.route != "splash_screen" &&
                currentDestination?.route != "authorization_screen") {
                AppBar(
                    title = when (currentDestination?.route) {
                        "main_screen" -> "Главная"
                        "booking_screen" -> "Бронирование"
                        "profile_screen" -> "Профиль"
                        "notifications_screen" -> "Уведомления"
                        else -> "Tennis App"
                    },
                    showBackButton = currentDestination?.route == "notifications_screen",
                    onBackClick = { navController.popBackStack() },
                    onNotificationsClick = { navController.navigate("notifications_screen") }
                )
            }
        },
        bottomBar = {
            if (currentDestination?.route != "splash_screen" &&
                currentDestination?.route != "notifications_screen" &&
                currentDestination?.route != "authorization_screen") {
                BottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable ("splash_screen") {
                SplashScreen(navController = navController)
            }
            composable ("main_screen") {
                MainContent()
            }
            composable ("booking_screen") {
                BookingContent()
            }
            composable ("profile_screen") {
                ProfileContent(navController = navController)
            }
            composable ("notifications_screen"){
                NotificationsContent()
            }
            composable("authorization_screen") {
                val context = LocalContext.current

                AuthorizationContent { phone, password ->
                    authorizeUser(
                        context = context,
                        phone = phone,
                        password = password,
                        onSuccess = { clientId ->
                            navController.navigate("main_screen") {
                                popUpTo("authorization_screen") { inclusive = true }
                            }
                        },
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}