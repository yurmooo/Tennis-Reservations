package com.example.tennisapp.ui.screens

import androidx.compose.foundation.background
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.ui.components.ProfileItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileContent(
    navController: NavController,
    userName: String = "Иван Иванов",
    email: String = "ivan@example.com",
    phone: String = "+7 (999) 123-45-67",
    onLogoutClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onMyBookingsClick: () -> Unit = {},
    onThemeClick: () -> Unit = {}
) {
    val context = LocalContext.current

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
                    .background(Color(0xFF4CAF50))
                    .clickable { onEditClick() },
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

        ProfileItem(
            icon = Icons.Default.Person,
            text = userName,
            onClick = onEditClick
        )

        ProfileItem(
            icon = Icons.Default.Email,
            text = email,
            onClick = onEditClick
        )

        ProfileItem(
            icon = Icons.Default.Phone,
            text = phone,
            onClick = onEditClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileItem(
            text = "Мои брони",
            icon = Icons.Default.List,
            onClick = onMyBookingsClick,
        )

        ProfileItem(
            text = "Тема",
            icon = Icons.Default.Settings,
            onClick = onThemeClick
        )

        Spacer(modifier = Modifier.weight(1f))

        ProfileItem(
            text = "Выход",
            icon = Icons.Default.ExitToApp,
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    UserDataStore.clearClientId(context)
                }
                navController.navigate("authorization_screen") {
                    popUpTo("main_screen") { inclusive = true }
                }
            }
        )
    }
}