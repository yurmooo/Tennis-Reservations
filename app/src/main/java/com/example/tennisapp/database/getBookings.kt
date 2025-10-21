package com.example.tennisapp.database

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

fun getBookings(
    context: Context,
    date: String,
    onSuccess: (List<String>) -> Unit,
    onError: (String) -> Unit
) {
    Log.d("GetBookings", "Запрос занятых слотов на дату: $date")

    val url = "http://10.0.2.2/get_bookings.php?date=$date"
    val queue = Volley.newRequestQueue(context)

    val request = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                if (response.getBoolean("success")) {
                    val bookedTimes = mutableListOf<String>()
                    val timesArray = response.getJSONArray("booked_times")

                    for (i in 0 until timesArray.length()) {
                        bookedTimes.add(timesArray.getString(i))
                    }

                    Log.d("GetBookings", "Найдено занятых слотов: ${bookedTimes.size}")
                    onSuccess(bookedTimes)
                } else {
                    val msg = response.getString("message")
                    Log.e("GetBookings", msg)
                    onError(msg)
                }
            } catch (e: Exception) {
                Log.e("GetBookings", "Ошибка обработки ответа: ${e.message}")
                onError("Ошибка обработки данных: ${e.message}")
            }
        },
        { error ->
            Log.e("GetBookings", "Сетевая ошибка: ${error.message}")
            onError(error.message ?: "Ошибка сети")
        }
    )
    queue.add(request)
}