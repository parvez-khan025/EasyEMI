package com.example.easyemi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easyemi.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

            cardNewSale.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_newSaleFragment)
            }
            cardProducts.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_productsFragment)
            }
            cardCustomers.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_customerFragment)
            }
            cardInstallments.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_installmentDetailsFragment)
            }
            cardTransactions.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_transactionFragment)
            }
            cardExpenses.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_expensesFragment)
            }
            cardVendors.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_vendorsFragment)
            }
            cardInventory.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_inventoryFragment)
            }
            }
        }
    }