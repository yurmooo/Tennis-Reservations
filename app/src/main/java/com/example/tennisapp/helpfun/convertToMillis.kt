package com.example.tennisapp.helpfun

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

internal fun convertToMillis(dateString: String?, timeString: String?, context: Context): Long {
    return try {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        val dateTimeString = "$dateString $timeString"
        val date = dateFormat.parse(dateTimeString)

        date?.time ?: run {
            Toast.makeText(context, "Ошибка формата даты", Toast.LENGTH_SHORT).show()
            System.currentTimeMillis() + 3600000
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при обработке даты", Toast.LENGTH_SHORT).show()
        System.currentTimeMillis() + 3600000
    }
}