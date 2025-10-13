package com.example.tennisapp.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class NotificationItem(val title: String, val message: String)

class NotificationsViewModel : ViewModel() {
    val notifications = mutableStateListOf<NotificationItem>()
    val hasNewNotification = mutableStateOf(false)

    fun addNotification(title: String, message: String) {
        notifications.add(0, NotificationItem(title, message))
        hasNewNotification.value = true
    }

    fun markAllAsRead() {
        hasNewNotification.value = false
    }
}
