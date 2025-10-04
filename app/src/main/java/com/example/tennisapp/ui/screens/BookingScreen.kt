package com.example.tennisapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tennisapp.roboto
import com.example.tennisapp.ui.components.PagerWeekCalendar

@Composable
fun BookingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PagerWeekCalendar(
            onDateSelected = { date ->
                // обработка выбранной даты
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Выберите корт или время для бронирования",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
    }
}