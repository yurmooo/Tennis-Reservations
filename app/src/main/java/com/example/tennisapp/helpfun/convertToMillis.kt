package com.example.tennisapp.helpfun

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

internal fun convertToMillis(dateString: String?, timeString: String?, context: Context): Long {
    return try {
        // Формат даты должен соответствовать тому, что вы используете в formattedSelectedDate
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        // Объединяем дату и время
        val dateTimeString = "$dateString $timeString"
        val date = dateFormat.parse(dateTimeString)

        date?.time ?: run {
            // Если не удалось распарсить, используем текущую дату + 1 час как запасной вариант
            Toast.makeText(context, "Ошибка формата даты", Toast.LENGTH_SHORT).show()
            System.currentTimeMillis() + 3600000
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Запасной вариант при ошибке
        Toast.makeText(context, "Ошибка при обработке даты", Toast.LENGTH_SHORT).show()
        System.currentTimeMillis() + 3600000
    }
}