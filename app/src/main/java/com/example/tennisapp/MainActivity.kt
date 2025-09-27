package com.example.tennisapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.ui.screens.AuthorizationContent
import com.example.tennisapp.ui.screens.MainScreen
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.getValue
import com.example.tennisapp.database.authorizeUser
import kotlinx.coroutines.launch

val roboto = FontFamily ( Font(R.font.roboto) )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val clientId by UserDataStore.getClientId(this@MainActivity)
                .collectAsState(initial = null)

            if (clientId != null) {
                MainScreen()
            } else {
                AuthorizationContent { phone, password ->
                    authorizeUser(
                        context = this,
                        phone = phone,
                        password = password,
                        onSuccess = { id ->
                            lifecycleScope.launch {
                                UserDataStore.saveClientId(this@MainActivity, id)
                            }
                        },
                        onError = { /* обработка ошибки */ }
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    MonthlyStats(
        hours = 12,
        maxHours = 50,
        visits = 7,
        maxVisits = 9
    )
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.LightGray,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = Color(0xFFFFC107),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$hours ч",
                    fontWeight = FontWeight.Bold,
                    fontFamily = roboto
                )
            }

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