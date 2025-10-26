package com.example.tennisapp.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.tennisapp.data.Trainer
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.database.createBooking
import com.example.tennisapp.database.getTrainers
import com.example.tennisapp.roboto
import com.example.tennisapp.utils.NotificationHelper
import com.example.tennisapp.helpfun.convertToMillis
import com.example.tennisapp.helpfun.convertToDatabaseFormat

@Composable
fun BookingSummaryScreen(
    sport: String,
    coach: String?,
    date: String?,
    time: String?,
    options: Set<String>,
    totalPrice: Int,
    notificationsViewModel: NotificationsViewModel,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val finalPrice = totalPrice
    val clientId by UserDataStore.getClientId(context).collectAsState(initial = null)
    var selectedTrainer: Trainer? = null
    var trainers by remember { mutableStateOf<List<Trainer>>(emptyList()) }

    LaunchedEffect(Unit) {
        getTrainers(context,
            onSuccess = { trainersList ->
                trainers = trainersList
            },
            onError = { error ->
                Log.e("BookingSummary", "Ошибка загрузки тренеров: $error")
            }
        )
    }

    val selectedTrainerId = remember(coach, trainers) {
        if (coach != null && coach != "Без тренера") {
            trainers.find { it.name == coach }?.id
        } else {
            null
        }
    }

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
            "Итоговая стоимость: $finalPrice ₽",
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
                    val startTimeMillis = convertToMillis(date, time, context)
                    val endTimeMillis = startTimeMillis + 60 * 60 * 1000
                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeMillis)
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMillis)
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

        Button(
            onClick = {
                if (sport.isNotEmpty() && !date.isNullOrEmpty() && !time.isNullOrEmpty()) {
                    val dbFormattedDate = convertToDatabaseFormat(date, time, context)

                    // Логи для отладки
                    Log.d("BookingDebug", "=== ДАННЫЕ БРОНИРОВАНИЯ ===")
                    Log.d("BookingDebug", "Спорт: $sport")
                    Log.d("BookingDebug", "Тренер: ${coach ?: "Без тренера"}")
                    Log.d("BookingDebug", "Дата (оригинал): $date")
                    Log.d("BookingDebug", "Время (оригинал): $time")
                    Log.d("BookingDebug", "Дата для БД: $dbFormattedDate")
                    Log.d("BookingDebug", "Опции: ${options.joinToString(", ")}")
                    Log.d("BookingDebug", "Client ID: ${clientId ?: 0}")
                    Log.d("BookingDebug", "Total Price: $totalPrice")
                    Log.d("BookingDebug", "Trainer ID: $selectedTrainerId")
                    Log.d("BookingDebug", "==========================")

                    createBooking(
                        context = context,
                        clientId = clientId ?: 0,
                        trainerId = selectedTrainerId,
                        sport = sport,
                        bookingTime = dbFormattedDate,
                        totalPrice = totalPrice,
                        options = options,
                        onSuccess = {
                            val message = "Вы забронировали $sport на ${date ?: ""} в ${time ?: ""}"
                            NotificationHelper.showBookingNotification(
                                context,
                                "Бронирование подтверждено",
                                message
                            )
                            notificationsViewModel.addNotification("Бронирование подтверждено", message)

                            Toast.makeText(
                                context,
                                "Бронирование успешно создано!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onConfirm()
                        },
                        onError = { errorMsg ->
                            Log.e("BookingError", errorMsg)
                            Toast.makeText(
                                context,
                                "Произошла ошибка при создании бронирования. Повторите попытку позже.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                } else {
                    Log.e("BookingDebug", "ОШИБКА: Не все поля заполнены")
                    Log.e("BookingDebug", "Спорт: $sport, Дата: $date, Время: $time")
                    Toast.makeText(
                        context,
                        "Заполните все обязательные поля: вид спорта, дата и время.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подтвердить бронирование", color = Color.White, fontFamily = roboto)
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