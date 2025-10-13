package com.example.tennisapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tennisapp.database.authorizeUser
import kotlinx.coroutines.launch

val roboto = FontFamily ( Font(R.font.roboto) )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

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

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    // Обработка результата запроса разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение предоставлено
                    Toast.makeText(this, "Разрешение на уведомления предоставлено", Toast.LENGTH_SHORT).show()
                } else {
                    // Разрешение отклонено
                    Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}