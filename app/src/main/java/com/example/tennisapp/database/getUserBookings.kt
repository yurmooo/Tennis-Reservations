package com.example.tennisapp.database

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tennisapp.data.Booking
import org.json.JSONArray

fun getUserBookings(
    context: Context,
    clientId: Int,
    onSuccess: (List<Booking>) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/get_user_bookings.php?client_id=$clientId"
    val queue = Volley.newRequestQueue(context)

    val request = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                if (response.getBoolean("success")) {
                    val bookingsList = mutableListOf<Booking>()
                    val jsonArray: JSONArray = response.getJSONArray("bookings")

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        bookingsList.add(
                            Booking(
                                id = obj.getInt("id"),
                                sport = obj.getString("sport"),
                                trainerName = obj.optString("trainer_name", "Без тренера"),
                                options = obj.optString("options", ""),
                                bookingTime = obj.getString("booking_time"),
                                status = obj.optString("status", "booked"),
                                createdAt = obj.optString("created_at", "")
                            )
                        )
                    }

                    onSuccess(bookingsList)
                } else {
                    onError(response.optString("message", "Ошибка при получении данных"))
                }
            } catch (e: Exception) {
                Log.e("GetUserBookings", "Ошибка обработки ответа: ${e.message}")
                onError("Ошибка обработки данных: ${e.message}")
            }
        },
        { error ->
            Log.e("GetUserBookings", "Ошибка сети: ${error.message}")
            onError(error.message ?: "Ошибка сети")
        }
    )
    queue.add(request)
}