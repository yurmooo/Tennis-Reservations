package com.example.tennisapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tennisapp.ui.screens.BookingContent
import com.example.tennisapp.ui.screens.MainContent
import com.example.tennisapp.ui.screens.NotificationsContent
import com.example.tennisapp.ui.screens.ProfileContent
import com.example.tennisapp.ui.screens.SplashScreen

@Composable
fun AppNavHost (navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen",
        modifier = modifier
    ) {
        composable("splash_screen") { SplashScreen(navController) }
        composable("main_screen") { MainContent() }
        composable("booking_screen") { BookingContent() }
        composable("profile_screen") { ProfileContent(navController = navController) }
        composable("notifications_screen") { NotificationsContent() }
    }
}