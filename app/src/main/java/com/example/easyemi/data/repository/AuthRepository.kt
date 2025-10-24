package com.example.easyemi.data.repository

import com.example.easyemi.core.Nodes
import com.example.easyemi.data.models.UserLogin
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.data.service.AuthService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject

class AuthRepository @Inject constructor(

    private val jAuth: FirebaseAuth,
    private val db: FirebaseFirestore

) : AuthService {

    override fun userRegistration(user: UserRegistration): Task<AuthResult> {
        return jAuth.createUserWithEmailAndPassword(user.email, user.password)
    }

    override fun userLogin(user: UserLogin): Task<AuthResult> {
        return jAuth.signInWithEmailAndPassword(user.email, user.password)
    }

    override fun createUser(user: UserRegistration): Task<Void> {
        return db.collection(Nodes.USERS).document(user.userID).set(user)

    }
}