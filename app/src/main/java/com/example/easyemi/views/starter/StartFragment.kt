package com.example.easyemi.views.starter

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.base.BaseFragment
import com.example.easyemi.databinding.FragmentStartBinding
import com.google.firebase.auth.FirebaseAuth

class StartFragment : BaseFragment<FragmentStartBinding>(FragmentStartBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Check for auto login when app starts
        setAutoLogin()
    }

    private fun setAutoLogin() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.isEmailVerified) {
            // ✅ Already logged in → Go to Dashboard
            findNavController().navigate(R.id.action_startFragment_to_dashboardFragment)
        }
    }

    override fun setListener() {
        with(binding) {
            loginButton.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_loginFragment)
            }
            registerButton.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_registerFragment)
            }
        }
    }

    override fun allObserver() {
        // Not needed for now
    }
}