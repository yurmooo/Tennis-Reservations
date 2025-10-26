package com.example.tennisapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import com.example.tennisapp.data.Booking
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.database.cancelBooking
import com.example.tennisapp.database.getUserBookings
import com.example.tennisapp.roboto
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MyBookingsScreen() {
    val context = LocalContext.current
    val clientId by UserDataStore.getClientId(context).collectAsState(initial = null)
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedBookingId by remember { mutableStateOf<Int?>(null) }

    fun loadBookings() {
        clientId?.let {
            isLoading = true
            getUserBookings(
                context = context,
                clientId = it,
                onSuccess = { list ->
                    val updatedList = list.map { booking ->
                        val isPast = try {
                            val format = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            val bookingDate = format.parse(booking.bookingTime)
                            val now = Date()
                            bookingDate?.before(now) == true
                        } catch (e: Exception) {
                            false
                        }

                        if (isPast && booking.status.lowercase() == "booked") {
                            booking.copy(status = "Completed")
                        } else booking
                    }

                    bookings = updatedList
                    isLoading = false
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            )
        }
    }

    LaunchedEffect(clientId) {
        loadBookings()
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(padding)
        ) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }

                bookings.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Нет активных бронирований",
                        fontFamily = roboto,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings, key = { it.id }) { booking ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            BookingCard(
                                booking = booking,
                                onCancel = { id ->
                                    selectedBookingId = id
                                    showCancelDialog = true
                                }
                            )
                        }
                    }
                }
            }

            if (showCancelDialog && selectedBookingId != null) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text("Отменить бронирование?", fontFamily = roboto) },
                    text = { Text("Вы уверены, что хотите отменить это бронирование?", fontFamily = roboto) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showCancelDialog = false
                                selectedBookingId?.let { id ->
                                    cancelBooking(
                                        context,
                                        id,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Бронирование отменено",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadBookings()
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        ) {
                            Text("Да, отменить", color = Color(0xFFD32F2F), fontFamily = roboto)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog = false }) {
                            Text("Отмена", color = Color.Gray, fontFamily = roboto)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking, onCancel: (Int) -> Unit) {
    val statusColor = when (booking.status.lowercase()) {
        "booked" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "cancelled" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    val sportEmoji = when (booking.sport.lowercase()) {
        "теннис" -> "🎾"
        "падел" -> "🥎"
        else -> "🏸"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(sportEmoji, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }

                Text(
                    booking.sport,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = roboto
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${booking.totalPrice} ₽",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = roboto,
                        color = Color(0xFF4CAF50)
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Тренер: ${booking.trainerName ?: "Без тренера"}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF333333), fontFamily = roboto)
            )

            if (!booking.options.isNullOrEmpty()) {
                Text(
                    "Опции: ${booking.options}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF666666), fontFamily = roboto)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("🗓 ${booking.bookingTime}", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = roboto, color = Color(0xFF444444)))
            Text("📅 Создано: ${booking.createdAt}", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontFamily = roboto))

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (booking.status.lowercase()) {
                        "completed" -> "ЗАВЕРШЕНО ✅"
                        "booked" -> "АКТИВНО ✅"
                        "cancelled" -> "ОТМЕНЕНО ❌"
                        else -> booking.status.uppercase()
                    },
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = roboto
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AnimatedVisibility(
                    visible = booking.status.lowercase() == "booked",
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    OutlinedButton(
                        onClick = { onCancel(booking.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.3.dp)
                    ) {
                        Text("Отменить", fontFamily = roboto)
                    }
                }
            }
        }
    }
}