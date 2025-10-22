package com.example.tennisapp.database

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

fun cancelBooking(
    context: Context,
    bookingId: Int,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/cancel_booking.php"
    val queue = Volley.newRequestQueue(context)

    val request = object : StringRequest(
        Method.POST, url,
        { response ->
            Log.d("CancelBooking", response)
            if (response.contains("\"success\":true")) {
                onSuccess()
            } else {
                onError("Не удалось отменить бронирование.")
            }
        },
        { error ->
            Log.e("CancelBooking", "Ошибка: ${error.message}")
            onError(error.message ?: "Ошибка сети")
        }
    ) {
        override fun getParams(): MutableMap<String, String> {
            return hashMapOf("booking_id" to bookingId.toString())
        }
    }

    queue.add(request)
}