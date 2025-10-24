package com.example.easyemi.views.register

import android.util.Log
import android.util.Patterns
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.base.BaseFragment
import com.example.easyemi.core.DataState
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.databinding.FragmentRegisterBinding
import com.example.easyemi.isEmpty
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
    }

    override fun setListener() {
        with(binding) {
            loginTV.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            registerBtn.setOnClickListener {
                if (validateInputs()) {
                    val email = etEmail.text.toString().trim()
                    val phone = formatPhoneNumber(etNum.text.toString().trim())
                    val password = etPass.text.toString().trim()

                    val user = UserRegistration(
                        name = etName.text.toString(),
                        email = email,
                        phoneNumber = phone,
                        password = password,
                        address = etAddress.text.toString(),
                        role = "Seller",
                        userID = ""
                    )

                    viewModel.userRegistration(user)
                } else {
                    showToast("Please fill all fields")
                }
            }
        }
    }

    override fun allObserver() {
        viewModel.registrationResponse.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Loading -> {
                }
                is DataState.Success -> {
                    showToast("Registration successful. Verification email sent to ${state.data.email}")
                    findNavController().navigate(R.id.action_registerFragment_to_emailverificationFragment)
                }
                is DataState.Error -> {
                    showToast("Error: ${state.message}")
                    Log.e("RegisterFragment", "Registration error: ${state.message}")
                }
                else -> Unit
            }
        }
    }

    private fun validateInputs(): Boolean = with(binding) {
        var valid = true
        listOf(etName, etEmail, etNum, etPass, etAddress).forEach {
            if (it.isEmpty()) valid = false
        }

        val email = etEmail.text.toString().trim()
        val password = etPass.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address")
            return false
        }

        if (password.length < 8) {
            showToast("Password must be at least 8 characters long")
            return false
        }

        valid
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