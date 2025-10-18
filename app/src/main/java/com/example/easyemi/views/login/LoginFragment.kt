package com.example.easyemi.views.login

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.base.BaseFragment
import com.example.easyemi.data.models.UserLogin
import com.example.easyemi.databinding.FragmentLoginBinding
import com.example.easyemi.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private lateinit var auth: FirebaseAuth

    override fun setListener() {
        auth = FirebaseAuth.getInstance()

        with(binding) {
            registerTV.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            loginBtn.setOnClickListener {
                etEmail.isEmpty()
                etPass.isEmpty()

                if (!etEmail.isEmpty() && !etPass.isEmpty()) {
                    val email = etEmail.text.toString().trim()
                    val password = etPass.text.toString().trim()
                    loginUser(email, password)
                }
            }
        }
    }

    override fun allObserver() {
        // No observers needed since everything is handled in fragment
    }

    private fun loginUser(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginBtn.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.loginBtn.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    handleLoginSuccess(user)
                } else {
                    Log.e("LoginFragment", "Login failed: ${task.exception?.message}")
                    showToast("Login failed: ${task.exception?.message}")
                }
            }
    }

    private fun handleLoginSuccess(user: FirebaseUser?) {
        if (user != null) {
            if (user.isEmailVerified) {
                showToast("Login successful")
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            } else {
                showToast("Please verify your email before logging in.")
            }
        } else {
            showToast("Login failed: User not found.")
        }
    }
}