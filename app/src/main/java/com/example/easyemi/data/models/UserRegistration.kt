package com.example.easyemi.data.models

data class UserRegistration(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val address: String,
    val role: String,
    var userID: String
)