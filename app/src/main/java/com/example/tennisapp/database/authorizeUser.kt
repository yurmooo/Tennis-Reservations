package com.example.tennisapp.database

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun authorizeUser(
    context: Context,
    phone: String,
    password: String,
    onSuccess: (Int) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/auth.php"
    val queue = Volley.newRequestQueue(context)

    val cleanedPhone = phone.replace("[^+\\d]".toRegex(), "")
    val fullPhone = if (cleanedPhone.startsWith("+")) cleanedPhone else "+7$cleanedPhone"

    val request = object : StringRequest(Method.POST, url,
        Response.Listener { response ->
            Log.d("API_SUCCESS", "Response: $response")
            try {
                val json = JSONObject(response)
                if (json.getBoolean("success")) {
                    val clientId = json.getInt("client_id")
                    val phone = json.getString("phone")
                    val email = if (json.isNull("email")) "" else json.getString("email")
                    val name = if (json.isNull("name")) "" else json.getString("name")

                    // Сохраняем данные
                    onSuccess(clientId)
                } else {
                    onError(json.getString("message"))
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "JSON parsing error: ${e.message}")
                onError("Ошибка обработки ответа сервера")
            }
        },
        Response.ErrorListener { error ->
            handleVolleyError(error, onError)
        }) {

        override fun getBodyContentType(): String {
            return "application/x-www-form-urlencoded; charset=UTF-8"
        }

        override fun getParams(): MutableMap<String, String> {
            return hashMapOf(
                "phone" to fullPhone,
                "password" to password
            )
        }
    }
    queue.add(request)
}

private fun handleVolleyError(error: VolleyError, onError: (String) -> Unit) {
    Log.e("VOLLEY_ERROR", "Error: ${error.message}")

    if (error.networkResponse != null) {
        val statusCode = error.networkResponse.statusCode
        val responseData = String(error.networkResponse.data, Charsets.UTF_8)

        Log.e("VOLLEY_ERROR", "Status code: $statusCode")
        Log.e("VOLLEY_ERROR", "Response: $responseData")

        when (statusCode) {
            400 -> {
                try {
                    val json = JSONObject(responseData)
                    onError(json.getString("message"))
                } catch (e: Exception) {
                    onError("Ошибка запроса: $responseData")
                }
            }
            401 -> onError("Неавторизованный доступ")
            404 -> onError("Сервер не найден")
            500 -> onError("Ошибка сервера")
            else -> onError("Ошибка сети: $statusCode")
        }
    } else {
        onError(error.message ?: "Неизвестная ошибка сети")
    }
}