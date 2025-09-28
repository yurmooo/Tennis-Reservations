package com.example.tennisapp.ui.screens

import androidx.compose.foundation.background
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.database.getUserProfile
import com.example.tennisapp.ui.components.InputField
import com.example.tennisapp.ui.components.ProfileItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.tennisapp.database.updateUserProfile
import com.example.tennisapp.roboto

@Composable
fun ProfileContent(
    navController: NavController,
    onMyBookingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onThemeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("Загрузка...") }
    var email by remember { mutableStateOf("Загрузка...") }
    var phone by remember { mutableStateOf("Загрузка...") }
    var clientId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        clientId = UserDataStore.getClientId(context).firstOrNull()

        clientId?.let {
            getUserProfile(
                context = context,
                clientId = it,
                onSuccess = { user ->
                    userName = user.name ?: ""
                    email = user.email ?: ""
                    phone = user.phone
                },
                onError = {
                    userName = "Ошибка"
                    email = "Ошибка"
                    phone = "Ошибка"
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Аватар",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        InputField(
            icon = Icons.Default.Person,
            value = userName,
            label = "Имя",
            onValueChange = { userName = it },
            onSave = { value ->
                clientId?.let {
                    updateUserProfile(context, it, value, email, { user ->
                        userName = user.name ?: ""
                    }, { })
                }
            }
        )

        InputField(
            icon = Icons.Default.Email,
            value = email,
            label = "Email",
            onValueChange = { email = it },
            onSave = { value ->
                clientId?.let {
                    updateUserProfile(context, it, userName, value, { user ->
                        email = user.email ?: ""
                    }, { })
                }
            }
        )

        ProfileItem(icon = Icons.Default.Phone, text = phone, onClick = onEditClick)

        Spacer(modifier = Modifier.height(12.dp))

        ProfileItem(text = "Мои брони", icon = Icons.Default.List, onClick = onMyBookingsClick)
        ProfileItem(text = "Тема", icon = Icons.Default.Settings, onClick = onThemeClick)

        Spacer(modifier = Modifier.weight(1f))

        ProfileItem(
            text = "Выход",
            icon = Icons.Default.ExitToApp,
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    UserDataStore.clearClientId(context)
                }
                navController.navigate("authorization_screen") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}