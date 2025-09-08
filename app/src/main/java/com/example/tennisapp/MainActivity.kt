package com.example.tennisapp

import android.graphics.drawable.Icon
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import java.nio.file.WatchEvent

val roboto = FontFamily ( Font(R.font.roboto) )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold (
        topBar = {
            if (currentDestination?.route != "splash_screen") {
                AppBar(
                    title = when (currentDestination?.route) {
                        "main_screen" -> "Главная"
                        "booking_screen" -> "Бронирование"
                        "profile_screen" -> "Профиль"
                        "notifications_screen" -> "Уведомления"
                        else -> "Tennis App"
                    },
                    onNotificationsClick = {
                        navController.navigate("notifications_screen")
                    }
                )
            }
        },
        bottomBar = {
            if (currentDestination?.route != "splash_screen" &&
                currentDestination?.route != "notifications_screen") {
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
                ProfileContent()
            }
            composable ("notifications_screen"){
                NotificationsContent()
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // Анимация
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }))
        delay(3000L)
        navController.navigate("main_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    // Изображение
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.ic_tennis_men),
            contentDescription = "Логотип",
            modifier = Modifier.scale(scale.value))
    }
}

@Composable
fun MainContent() {
    MonthlyStats( //Передать реальные данные
        hours = 12,
        maxHours = 50,
        visits = 7,
        maxVisits = 9
    )
}

@Composable
fun BookingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Booking Screen", fontSize = 24.sp, fontFamily = roboto)
    }
}

@Composable
fun ProfileContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Screen", fontSize = 24.sp, fontFamily = roboto)
    }
}

@Composable
fun NotificationsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Notifications Screen", fontSize = 24.sp, fontFamily = roboto)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String = "Tennis App",
    onNotificationsClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Уведомления"
                )
            }
        }
    )
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

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
                    Text(text = item.title)
                }
            )
        }
    }
}

@Composable
fun MonthlyStats(
    hours: Int,
    maxHours: Int = 50,
    visits: Int,
    maxVisits: Int
) {
    val sweepAngle = 360f * hours / maxHours

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp), //отступы у карточек
        shape = RoundedCornerShape(16.dp), //закругленные углы
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) //Тень
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Левая часть — круг с часами
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Сначала рисуем серый "фон"
                    drawArc(
                        color = Color.LightGray,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )

                    // Затем поверх — прогресс
                    drawArc(
                        color = Color(0xFFFFC107),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )
                }

                // Текст в центре
                Text(
                    text = "$hours ч",
                    fontWeight = FontWeight.Bold,
                    fontFamily = roboto
                )
            }

            // Правая часть — кружки с посещениями
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f).padding(start = 15.dp)
            ) {
                Text(
                    text = "Посещения за месяц",
                    style = TextStyle(
                        fontFamily = roboto,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(maxVisits) { index ->
                        val filled = index < visits
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (filled) Color(0xFFFFC107) else Color.Transparent)
                                .border(2.dp, Color(0xFFFFC107), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$visits/$maxVisits посещений",
                    style = TextStyle(
                        fontFamily = roboto,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}