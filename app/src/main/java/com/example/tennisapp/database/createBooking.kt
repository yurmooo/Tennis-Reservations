package com.example.tennisapp.database

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun createBooking(
    context: Context,
    clientId: Int,
    trainerId: Int?,
    sport: String,
    bookingTime: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/create_booking.php"
    val queue = Volley.newRequestQueue(context)

    val json = JSONObject().apply {
        put("client_id", clientId)
        put("trainer_id", trainerId ?: JSONObject.NULL)
        put("sport", sport)
        put("booking_time", bookingTime)
        put("duration_minutes", 60)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, url, json,
        { response ->
            try {
                if (response.getBoolean("success")) {
                    onSuccess(response.getString("message"))
                } else {
                    val msg = response.getString("message")
                    Log.e("BookingError", msg)
                    onError(msg)
                }
            } catch (e: Exception) {
                Log.e("BookingError", "Ошибка обработки ответа: ${e.message}")
                onError("Ошибка обработки ответа: ${e.message}")
            }
        },
        { error ->
            Log.e("BookingError", "Сетевая ошибка: ${error.message}")
            onError(error.message ?: "Ошибка сети")
        }
    )

    queue.add(request)
}