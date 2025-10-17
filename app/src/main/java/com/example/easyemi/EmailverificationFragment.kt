package com.example.easyemi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.easyemi.databinding.FragmentEmailverificationBinding
import com.google.firebase.auth.FirebaseAuth

class EmailverificationFragment : Fragment() {

    private lateinit var binding: FragmentEmailverificationBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailverificationBinding.inflate(inflater, container, false)
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        with(binding) {
            btnDone.setOnClickListener {
                auth.currentUser?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            Toast.makeText(requireContext(), "Email verified successfully!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_emailverificationFragment_to_loginFragment)
                        } else {
                            Toast.makeText(requireContext(), "Email not verified yet. Please check your inbox.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to refresh user info. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}