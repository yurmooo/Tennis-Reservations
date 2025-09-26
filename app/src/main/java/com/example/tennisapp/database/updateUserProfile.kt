package com.example.tennisapp.database

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tennisapp.data.User
import org.json.JSONObject

fun updateUserProfile(
    context: Context,
    clientId: Int,
    name: String,
    email: String,
    onSuccess: (User) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/profile.php"
    val queue = Volley.newRequestQueue(context)

    val request = object : StringRequest(
        Method.POST,
        url,
        Response.Listener { response ->
            try {
                val json = JSONObject(response)
                if (json.getBoolean("success")) {
                    val updatedUser = User(
                        id = json.getInt("client_id"),
                        phone = json.getString("phone"),
                        email = json.optString("email", null),
                        name = json.optString("name", null)
                    )
                    onSuccess(updatedUser)
                } else {
                    onError(json.getString("message"))
                }
            } catch (e: Exception) {
                onError("Ошибка обработки ответа")
            }
        },
        Response.ErrorListener { error ->
            onError(error.message ?: "Ошибка сети")
        }
    ) {
        override fun getParams(): MutableMap<String, String> {
            return hashMapOf(
                "client_id" to clientId.toString(),
                "name" to name,
                "email" to email
            )
        }
    }
    queue.add(request)
}