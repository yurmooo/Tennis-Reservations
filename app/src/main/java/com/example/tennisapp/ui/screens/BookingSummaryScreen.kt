package com.example.tennisapp.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tennisapp.roboto

@Composable
fun BookingSummaryScreen(
    sport: String,
    coach: String?,
    date: String?,
    time: String?,
    options: Set<String>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val basePrice = if (sport == "Теннис") 1500 else 1800
    val coachPrice = if (coach != null && coach != "Без тренера") 800 else 0
    val optionsPrice = options.size * 200
    val totalPrice = basePrice + coachPrice + optionsPrice

    BackHandler {
        showExitDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Подтверждение бронирования",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = roboto
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        SummaryItem("Вид спорта", sport)
        SummaryItem("Тренер", coach ?: "Без тренера")
        SummaryItem("Дата", date ?: "Не выбрана")
        SummaryItem("Время", time ?: "Не выбрано")

        if (options.isNotEmpty()) {
            SummaryItem("Дополнительные опции", options.joinToString(", "))
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            "Итоговая стоимость: $totalPrice ₽",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = roboto,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(CalendarContract.Events.TITLE, "$sport (${coach ?: "Без тренера"})")
                    putExtra(
                        CalendarContract.Events.DESCRIPTION,
                        "Бронирование корта: $sport"
                    )
                    putExtra(CalendarContract.Events.EVENT_LOCATION, "Tennis & Padel Club")
                    val startTime = System.currentTimeMillis() + 3600000
                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime + 60 * 60 * 1000)
                }
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить в календарь", color = Color.White, fontFamily = roboto)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onConfirm() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подтвердить бронирование", fontFamily = roboto)
        }

        TextButton(
            onClick = { showExitDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Отменить", color = Color.Gray, fontFamily = roboto)
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Отменить бронирование?", fontFamily = roboto) },
            text = {
                Text(
                    "Если вы выйдете, все выбранные данные будут потеряны.",
                    fontFamily = roboto
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onCancel()
                    }
                ) {
                    Text("Выйти", color = Color(0xFFD32F2F), fontFamily = roboto)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Остаться", color = Color(0xFF4CAF50), fontFamily = roboto)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun SummaryItem(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontFamily = roboto
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = roboto
            )
        )
    }
}