package com.example.easyemi.views.register

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.data.repository.AuthRepository

class RegistrationViewModel: ViewModel() {

    fun userRegistration(user: UserRegistration) {

        val authService = AuthRepository()

        authService.userRegistration(user).addOnSuccessListener {
            Log.d("TAG", "Registration Successful")
        }.addOnFailureListener { error->
            Log.d("TAG", "Registration Failed")

        }

    }
}