package com.example.tennisapp.database

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tennisapp.data.User
import org.json.JSONObject

fun getUserProfile(
    context: Context,
    clientId: Int,
    onSuccess: (User) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/profile.php"
    val queue = Volley.newRequestQueue(context)

    val request = object : StringRequest(Method.POST, url,
        Response.Listener { response ->
            try {
                android.util.Log.d("PROFILE_API", "Response: $response")
                val json = JSONObject(response)
                if (json.getBoolean("success")) {
                    val user = User(
                        id = json.getInt("client_id"),
                        phone = json.getString("phone"),
                        email = if (json.isNull("email")) null else json.getString("email"),
                        name = if (json.isNull("name")) null else json.getString("name")
                    )
                    onSuccess(user)
                } else {
                    onError(json.getString("message"))
                }
            } catch (e: Exception) {
                android.util.Log.e("PROFILE_API", "JSON error: ${e.message}")
                onError("Ошибка обработки ответа")
            }
        },
        Response.ErrorListener { error ->
            onError(error.message ?: "Ошибка сети")
        }
    ) {
        override fun getParams(): MutableMap<String, String> {
            return hashMapOf(
                "client_id" to clientId.toString()
            )
        }
    }
    queue.add(request)
}