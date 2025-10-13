package com.example.tennisapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.tennisapp.roboto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String = "Tennis App",
    onNotificationsClick: () -> Unit = {},
    showBackButton: Boolean = false,
    showMarkReadButton: Boolean = false,
    onMarkRead: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title, fontFamily = roboto)
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            }
        },
        actions = {
            when {
                showMarkReadButton -> {
                    IconButton(onClick = onMarkRead) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Отметить как прочитанные"
                        )
                    }
                }
                !showBackButton -> {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Уведомления"
                        )
                    }
                }
            }
        }
    )
}