package com.example.easyemi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easyemi.databinding.FragmentEmailverificationBinding

class EmailverificationFragment : Fragment() {
    private lateinit var binding: FragmentEmailverificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmailverificationBinding.inflate(inflater, container, false)
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding){
            findNavController().navigate(R.id.action_emailverificationFragment_to_loginFragment)
        }
    }

}