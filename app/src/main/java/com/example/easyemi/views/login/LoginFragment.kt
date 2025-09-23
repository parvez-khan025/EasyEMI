package com.example.easyemi.views.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.databinding.FragmentLoginBinding
import com.example.easyemi.isEmpty

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding){
            registerTV.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            loginBtn.setOnClickListener{
                etEmail.isEmpty()
                etPass.isEmpty()

                if(!etEmail.isEmpty() && !etPass.isEmpty()){
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                }
            }
            }
        }
    }
