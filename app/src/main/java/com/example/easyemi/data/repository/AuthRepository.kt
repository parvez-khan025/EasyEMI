package com.example.easyemi.data.repository

import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.data.service.AuthService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthRepository : AuthService{
    override fun userRegistration(user: UserRegistration): Task<AuthResult> {
        val jauth = FirebaseAuth.getInstance()

        return jauth.createUserWithEmailAndPassword(user.email, user.password)
    }

    override fun userLogin() {

    }
}