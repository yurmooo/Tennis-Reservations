package com.example.tennisapp.helpfun

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

internal fun convertToDatabaseFormat(dateString: String?, timeString: String?, context: Context): String {
    return try {
        Log.d("DateConversion", "Начало конвертации: dateString='$dateString', timeString='$timeString'")

        // Парсим из формата "dd.MM.yyyy HH:mm"
        val inputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateTimeString = "$dateString $timeString"
        Log.d("DateConversion", "Объединенная строка: '$dateTimeString'")

        val date = inputFormat.parse(dateTimeString)
        Log.d("DateConversion", "Распарсена дата: $date")

        // Попробуем разные форматы для БД
        val formatsToTry = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "dd.MM.yyyy HH:mm:ss" // оригинальный формат
        )

        var result = ""
        for (format in formatsToTry) {
            val outputFormat = SimpleDateFormat(format, Locale.getDefault())
            result = outputFormat.format(date ?: Date())
            Log.d("DateConversion", "Формат '$format': '$result'")
        }

        // Используем первый формат по умолчанию
        val finalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val finalResult = finalFormat.format(date ?: Date())
        Log.d("DateConversion", "Финальный результат для БД: '$finalResult'")

        finalResult

    } catch (e: Exception) {
        Log.e("DateConversion", "Ошибка конвертации даты", e)
        Toast.makeText(context, "Ошибка формата даты", Toast.LENGTH_SHORT).show()
        val fallbackFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fallbackResult = fallbackFormat.format(Date())
        Log.e("DateConversion", "Используем запасной вариант: '$fallbackResult'")
        fallbackResult
    }
}