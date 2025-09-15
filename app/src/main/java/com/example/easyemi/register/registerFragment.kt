package com.example.easyemi.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.databinding.FragmentRegisterBinding
import com.example.easyemi.databinding.FragmentStartBinding

class registerFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding){
            loginTV.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

    }
}