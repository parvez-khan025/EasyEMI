package com.example.easyemi.views.register

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.base.BaseFragment
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.databinding.FragmentRegisterBinding
import com.example.easyemi.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance() // Firestore instance

    override fun setListener() {
        auth = FirebaseAuth.getInstance()

        with(binding) {

            loginTV.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            registerBtn.setOnClickListener {

                etName.isEmpty()
                etEmail.isEmpty()
                etNum.isEmpty()
                etPass.isEmpty()
                etAddress.isEmpty()

                if (!etName.isEmpty() && !etEmail.isEmpty() && !etNum.isEmpty()
                    && !etPass.isEmpty() && !etAddress.isEmpty()
                ) {

                    val email = etEmail.text.toString().trim()
                    val phone = formatPhoneNumber(etNum.text.toString().trim())
                    val password = etPass.text.toString().trim()

                    // Check if phone already exists
                    db.collection("users")
                        .whereEqualTo("phoneNumber", phone)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (!snapshot.isEmpty) {
                                showToast("Phone number already registered")
                            } else {
                                createFirebaseUser(email, password, phone)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("RegisterFragment", "Phone check failed", e)
                            showToast("Error checking phone number")
                        }

                } else {
                    showToast("Please fill all fields")
                }
            }
        }
    }

    override fun allObserver() {
        // No live data observer needed here
    }

    private fun createFirebaseUser(email: String, password: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification()
                            .addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    showToast("Verification email sent to $email")

                                    // Save user in Firestore
                                    val user = UserRegistration(
                                        name = binding.etName.text.toString(),
                                        email = email,
                                        phoneNumber = phone,
                                        password = password,
                                        address = binding.etAddress.text.toString(),
                                        role = "Seller",
                                        userID = firebaseUser.uid
                                    )
                                    viewModel.userRegistration(user)
                                    db.collection("users").document(firebaseUser.uid).set(user)

                                    findNavController().navigate(R.id.action_registerFragment_to_emailverificationFragment)
                                } else {
                                    showToast("Failed to send verification email")
                                }
                            }
                    } else showToast("Registration failed: User is null")
                } else {
                    showToast("Registration failed: ${task.exception?.message}")
                }
            }
    }

    private fun formatPhoneNumber(phone: String): String {
        var formatted = phone.trim()
        return when {
            formatted.startsWith("0") -> "+88${formatted.substring(1)}"
            formatted.startsWith("+") -> formatted
            else -> "+$formatted"
        }
    }
}