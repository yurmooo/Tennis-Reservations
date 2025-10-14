package com.example.tennisapp.database

import android.content.Context
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tennisapp.data.Trainer
import org.json.JSONObject

fun getTrainers(
    context: Context,
    onSuccess: (List<Trainer>) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/get_trainers.php"
    val queue = Volley.newRequestQueue(context)

    val request = StringRequest(
        Method.GET, url,
        { response ->
            try {
                val json = JSONObject(response)
                if (json.getBoolean("success")) {
                    val trainersArray = json.getJSONArray("trainers")
                    val list = mutableListOf<Trainer>()

                    for (i in 0 until trainersArray.length()) {
                        val obj = trainersArray.getJSONObject(i)
                        list.add(
                            Trainer(
                                id = obj.getInt("id"),
                                name = obj.getString("name"),
                                specialization = obj.optString("specialization", null),
                                photoUrl = obj.optString("photo_url", null)
                            )
                        )
                    }
                    onSuccess(list)
                } else {
                    onError(json.getString("message"))
                }
            } catch (e: Exception) {
                onError("Ошибка обработки данных: ${e.message}")
            }
        },
        { error ->
            onError(error.message ?: "Ошибка сети")
        }
    )
    queue.add(request)
}