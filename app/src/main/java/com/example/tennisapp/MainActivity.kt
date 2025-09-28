package com.example.tennisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.ui.screens.AuthorizationContent
import com.example.tennisapp.ui.screens.MainScreen
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.getValue
import com.example.tennisapp.database.authorizeUser
import kotlinx.coroutines.launch

val roboto = FontFamily ( Font(R.font.roboto) )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val clientId by UserDataStore.getClientId(this@MainActivity)
                .collectAsState(initial = null)

            if (clientId != null) {
                MainScreen()
            } else {
                AuthorizationContent { phone, password ->
                    authorizeUser(
                        context = this,
                        phone = phone,
                        password = password,
                        onSuccess = { id ->
                            lifecycleScope.launch {
                                UserDataStore.saveClientId(this@MainActivity, id)
                            }
                        },
                        onError = { /* обработка ошибки */ }
                    )
                }
            }
        }
    }
}