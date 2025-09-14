package com.example.tennisapp.database

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tennisapp.data.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

fun AuthorizeUser(
    context: Context,
    phone: String,
    password: String,
    onSuccess: (Int) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://10.0.2.2/auth.php"
    val queue = Volley.newRequestQueue(context)

    val request = object : StringRequest(Method.POST, url,
        Response.Listener { response ->
            val json = JSONObject(response)
            if (json.getBoolean("success")) {
                val clientId = json.getInt("client_id")
                onSuccess(clientId)
                CoroutineScope(Dispatchers.IO).launch {
                    UserDataStore.saveClientId(context, clientId)
                }
            } else {
                onError(json.getString("message"))
            }
        },
        Response.ErrorListener { error ->
            onError(error.message ?: "Ошибка сети")
        }) {
        override fun getParams(): MutableMap<String, String> {
            return hashMapOf(
                "phone" to phone,
                "password" to password
            )
        }
    }
    queue.add(request)
}
