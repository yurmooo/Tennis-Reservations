package com.example.tennisapp.data

data class Booking(
    val id: Int,
    val sport: String,
    val trainerName: String?,
    val options: String?,
    val bookingTime: String,
    val status: String,
    val createdAt: String
)
