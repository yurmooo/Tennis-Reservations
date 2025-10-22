package com.example.tennisapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.tennisapp.data.Booking
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.database.cancelBooking
import com.example.tennisapp.database.getUserBookings
import com.example.tennisapp.roboto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen() {
    val context = LocalContext.current
    val clientId by UserDataStore.getClientId(context).collectAsState(initial = null)
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(clientId) {
        clientId?.let {
            getUserBookings(
                context = context,
                clientId = it,
                onSuccess = { list ->
                    bookings = list
                    isLoading = false
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои бронирования", fontFamily = roboto) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FA))
                .padding(padding)
        ) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                bookings.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет активных бронирований", fontFamily = roboto, color = Color.Gray)
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        BookingCard(
                            booking = booking,
                            onCancel = { id ->
                                cancelBooking(
                                    context,
                                    id,
                                    onSuccess = {
                                        Toast.makeText(context, "Бронирование отменено", Toast.LENGTH_SHORT).show()
                                        bookings = bookings.filterNot { it.id == id }
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking, onCancel: (Int) -> Unit) {
    val statusColor = when (booking.status) {
        "booked" -> Color(0xFF4CAF50)
        "cancelled" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                booking.sport,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = roboto
                )
            )

            Text(
                "Тренер: ${booking.trainerName ?: "Без тренера"}",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = roboto)
            )

            if (!booking.options.isNullOrEmpty()) {
                Text(
                    "Опции: ${booking.options}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = roboto)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("⏰ ${booking.bookingTime}", fontFamily = roboto, color = Color(0xFF444444))
            Text("📅 Создано: ${booking.createdAt}", fontFamily = roboto, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = booking.status.uppercase(),
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = roboto
                    )
                )

                AnimatedVisibility(
                    visible = booking.status == "booked",
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    OutlinedButton(
                        onClick = { onCancel(booking.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                    ) {
                        Text("Отменить", fontFamily = roboto)
                    }
                }
            }
        }
    }
}