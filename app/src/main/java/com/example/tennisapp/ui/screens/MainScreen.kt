package com.example.tennisapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tennisapp.ui.components.AppBar
import com.example.tennisapp.database.authorizeUser
import com.example.tennisapp.ui.components.BottomBar
import com.example.tennisapp.ui.components.CarouselSlider
import com.example.tennisapp.ui.components.MonthlyStats
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.example.tennisapp.ui.components.BookingButton

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
                MainContent(navController = navController)
            }
            composable ("booking_screen") {
                BookingContent(navController = navController)
            }
            composable ("summary_screen/{sport}/{coach}/{date}/{time}/{options}") { backStackEntry ->
                val sport = backStackEntry.arguments?.getString("sport") ?: ""
                val coach = backStackEntry.arguments?.getString("coach")
                val date = backStackEntry.arguments?.getString("date")
                val time = backStackEntry.arguments?.getString("time")
                val options = backStackEntry.arguments?.getString("options")?.split(";")?.toSet() ?: emptySet()

                BookingSummaryScreen(
                    sport = sport,
                    coach = coach,
                    date = date,
                    time = time,
                    options = options,
                    onConfirm = { navController.popBackStack("main_screen", inclusive = false) },
                    onCancel = { navController.popBackStack() } )
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

@Composable
fun MainContent(navController: NavController) {
    val images = listOf(
        "https://i.pinimg.com/736x/6d/c9/ae/6dc9ae126aa64a4d0522954f1f69bf32.jpg",
        "https://static.onlinetrade.ru/img/items/b/raketka_krafla_kid21_dlya_tennisa_1762371_1.jpg",
        "https://img.freepik.com/photos-premium/raquette-tennis-fond-rose_51524-13927.jpg"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CarouselSlider(images = images)

        MonthlyStats(hours = 12, maxHours = 50, visits = 7, maxVisits = 9)

        BookingButton(navController)
    }
}