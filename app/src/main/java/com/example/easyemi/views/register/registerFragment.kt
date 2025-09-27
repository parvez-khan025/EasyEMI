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

class registerFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

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
                    val password = etPass.text.toString().trim()

                    // Create user in Firebase
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val firebaseUser = auth.currentUser

                                if (firebaseUser != null) {
                                    Log.d("RegisterFragment", "User created: ${firebaseUser.email}")

                                    // Send verification email
                                    firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener { verifyTask ->
                                            if (verifyTask.isSuccessful) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Verification email sent to $email",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                // Save user AFTER verification email is sent
                                                val user = UserRegistration(
                                                    name = etName.text.toString(),
                                                    email = email,
                                                    phoneNumber = etNum.text.toString(),
                                                    password = password,
                                                    address = etAddress.text.toString(),
                                                    role = "Seller",
                                                    userID = firebaseUser.uid
                                                )
                                                viewModel.userRegistration(user)

                                                // Navigate to EmailVerificationFragment
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

                } else {
                    Log.e("RegisterFragment", "Please fill all fields")
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
