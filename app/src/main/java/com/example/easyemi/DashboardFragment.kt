package com.example.easyemi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easyemi.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding){
            icNewSale.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_newSaleFragment)
            }
            icProducts.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_productsFragment)
            }
            icCustomers.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_customerFragment)
            }

        }
    }
}