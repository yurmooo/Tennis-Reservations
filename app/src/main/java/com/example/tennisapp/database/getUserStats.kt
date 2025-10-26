package com.example.tennisapp.database

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

fun getUserStats(
    context: Context,
    clientId: Int,
    onSuccess: (hours: Int, visits: Int) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/get_user_stats.php?client_id=$clientId"
    val queue = Volley.newRequestQueue(context)

    val request = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                if (response.getBoolean("success")) {
                    val totalHours = response.optInt("total_hours", 0)
                    val totalVisits = response.optInt("total_visits", 0)
                    onSuccess(totalHours, totalVisits)
                } else {
                    onError(response.optString("message", "Ошибка при получении статистики"))
                }
            } catch (e: Exception) {
                Log.e("GetUserStats", "Ошибка обработки ответа: ${e.message}")
                onError("Ошибка обработки данных: ${e.message}")
            }
        },
        { error ->
            Log.e("GetUserStats", "Ошибка сети: ${error.message}")
            onError(error.message ?: "Ошибка сети")
        }
    )
    queue.add(request)
}