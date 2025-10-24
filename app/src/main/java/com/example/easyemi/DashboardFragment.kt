package com.example.easyemi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.easyemi.databinding.FragmentDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setupBottomNav()
    }

    private fun setListener() = with(binding) {
        cardNewSale.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_newSaleFragment)
        }
        cardProducts.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_productsFragment)
        }
        cardCustomers.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_customerFragment)
        }
        cardInstallments.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_installmentDetailsFragment)
        }
        cardTransactions.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_transactionFragment)
        }
        cardExpenses.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_expensesFragment)
        }
        cardVendors.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_vendorsFragment)
        }
        cardInventory.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_inventoryFragment)
        }
    }

    private fun setupBottomNav() {
        val bottomNav: BottomNavigationView = binding.bottomNav
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> true // already on this screen
                R.id.addProductFragment -> {
                    navController.navigate(R.id.addProductFragment)
                    true
                }
                R.id.userProfileFragment -> {
                    navController.navigate(R.id.userProfileFragment)
                    true
                }
                else -> false
            }
        }

        // keep current tab highlighted
        bottomNav.selectedItemId = R.id.dashboardFragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}