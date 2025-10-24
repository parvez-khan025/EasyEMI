package com.example.easyemi.views.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easyemi.core.DataState
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authService: AuthRepository
) : ViewModel() {

    private val _registrationResponse = MutableLiveData<DataState<UserRegistration>>()
    val registrationResponse: LiveData<DataState<UserRegistration>> = _registrationResponse

    private val auth = FirebaseAuth.getInstance()

    fun userRegistration(user: UserRegistration) {
        _registrationResponse.postValue(DataState.Loading)

        authService.userRegistration(user)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // ✅ Set UID from Firebase
                    user.userID = firebaseUser.uid

                    // ✅ Reload to ensure session is active before sending verification
                    firebaseUser.reload().addOnCompleteListener {
                        firebaseUser.sendEmailVerification()
                            .addOnSuccessListener {
                                Log.d("RegistrationViewModel", "Verification email sent to ${user.email}")

                                // ✅ Save user info in Firestore
                                authService.createUser(user)
                                    .addOnSuccessListener {
                                        _registrationResponse.postValue(DataState.Success(user))
                                    }
                                    .addOnFailureListener { e ->
                                        _registrationResponse.postValue(DataState.Error("Failed to save user: ${e.message}"))
                                    }
                            }
                            .addOnFailureListener { e ->
                                _registrationResponse.postValue(DataState.Error("Failed to send verification: ${e.message}"))
                            }
                    }
                } else {
                    _registrationResponse.postValue(DataState.Error("Registration failed: Firebase user is null"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("RegistrationViewModel", "Registration failed: ${e.message}", e)
                _registrationResponse.postValue(DataState.Error("Registration failed: ${e.message}"))
            }
    }
}