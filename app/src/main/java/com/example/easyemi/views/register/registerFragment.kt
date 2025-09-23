package com.example.easyemi.views.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.easyemi.R
import com.example.easyemi.data.models.UserRegistration
import com.example.easyemi.databinding.FragmentRegisterBinding
import com.example.easyemi.databinding.FragmentStartBinding
import com.example.easyemi.isEmpty

class registerFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val viewModel: RegistrationViewModel by viewModels()

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
            registerBtn.setOnClickListener {
                etName.isEmpty()
                etEmail.isEmpty()
                etNum.isEmpty()
                etPass.isEmpty()
                etAddress.isEmpty()

                if(!etName.isEmpty() && !etEmail.isEmpty() && !etNum.isEmpty() && !etPass.isEmpty() && !etAddress.isEmpty()){
                    val user = UserRegistration(
                        name = etName.text.toString(),
                        email = etEmail.text.toString(),
                        phoneNumber = etNum.text.toString(),
                        password = etPass.text.toString(),
                        address = etAddress.text.toString(),
                        role = "Seller",
                        userID = ""
                    )
                    
                    viewModel.userRegistration(user)

                }
            }
        }
    }
}