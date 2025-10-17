package com.example.easyemi.data.repository

import com.example.easyemi.data.models.UserLogin
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.data.service.AuthService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthRepository : AuthService {

    private val jAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Register user with email and password
    override fun userRegistration(user: UserRegistration): Task<AuthResult> {
        return jAuth.createUserWithEmailAndPassword(user.email, user.password)
    }

    override fun userLogin(user: UserLogin): Task<AuthResult> {
        return jAuth.signInWithEmailAndPassword(user.email, user.password)
    }

    fun getCurrentUser() = jAuth.currentUser

    fun signOut() = jAuth.signOut()
}
