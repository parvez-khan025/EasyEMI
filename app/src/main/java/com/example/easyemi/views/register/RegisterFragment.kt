package com.example.easyemi.views.register

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.databinding.FragmentRegisterBinding
import com.example.easyemi.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance() // Firestore instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding) {

            // Navigate to Login Page if user already has an account
            loginTV.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            registerBtn.setOnClickListener {

                // Check for empty fields using your custom extension
                etName.isEmpty()
                etEmail.isEmpty()
                etNum.isEmpty()
                etPass.isEmpty()
                etAddress.isEmpty()

                if (!etName.isEmpty() && !etEmail.isEmpty() && !etNum.isEmpty()
                    && !etPass.isEmpty() && !etAddress.isEmpty()
                ) {

                    val email = etEmail.text.toString().trim()
                    var phone = etNum.text.toString().trim()
                    val password = etPass.text.toString().trim()

                    // Convert local phone to international format
                    phone = formatPhoneNumber(phone)

                    // ✅ Step 1: Check if phone number is already used
                    db.collection("users")
                        .whereEqualTo("phoneNumber", phone)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (!snapshot.isEmpty) {
                                Toast.makeText(
                                    requireContext(),
                                    "Phone number already registered",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                // ✅ Step 2: Register with Firebase Auth
                                createFirebaseUser(email, password, phone)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("RegisterFragment", "Failed to check phone uniqueness", e)
                            Toast.makeText(
                                requireContext(),
                                "Error checking phone number",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {
                    Log.e("RegisterFragment", "Please fill all fields")
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    // ✅ Create user in Firebase Authentication
    private fun createFirebaseUser(email: String, password: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        Log.d("RegisterFragment", "User created: ${firebaseUser.email}")

                        // ✅ Step 3: Send email verification
                        firebaseUser.sendEmailVerification()
                            .addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Verification email sent to $email",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    // ✅ Step 4: Save user data to Firestore
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

                                    db.collection("users")
                                        .document(firebaseUser.uid)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Log.d("RegisterFragment", "User saved to Firestore")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("RegisterFragment", "Firestore save failed", e)
                                        }

                                    // ✅ Step 5: Navigate to Email Verification Screen
                                    findNavController().navigate(
                                        R.id.action_registerFragment_to_emailverificationFragment
                                    )

                                } else {
                                    Log.e(
                                        "RegisterFragment",
                                        "Failed to send verification email: ${verifyTask.exception?.message}"
                                    )
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to send verification email",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegisterFragment", "Email verification failed", e)
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to send verification email: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    } else {
                        Log.e("RegisterFragment", "Current user is null after registration")
                        Toast.makeText(
                            requireContext(),
                            "Registration failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Log.e("RegisterFragment", "Registration failed: ${task.exception?.message}")
                    Toast.makeText(
                        requireContext(),
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // ✅ Convert Bangladeshi phone numbers to +880 format
    private fun formatPhoneNumber(phone: String): String {
        var formatted = phone.trim()
        return when {
            formatted.startsWith("0") -> "+88" + formatted.substring(1)
            formatted.startsWith("+") -> formatted
            else -> "+$formatted"
        }
    }
}