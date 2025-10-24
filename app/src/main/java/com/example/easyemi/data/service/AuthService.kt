package com.example.easyemi.data.service

import com.example.easyemi.data.models.UserLogin
import com.example.easyemi.data.models.UserRegistration
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

interface AuthService {

    fun userRegistration(user: UserRegistration): Task<AuthResult>
    fun userLogin(user: UserLogin): Task<AuthResult>

    fun createUser(user: UserRegistration): Task<Void>
}