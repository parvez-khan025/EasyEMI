package com.example.easyemi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyemi.addProduct.ProductAdapter
import com.example.easyemi.data.models.Product
import com.example.easyemi.databinding.FragmentProductsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private lateinit var adapter: ProductAdapter
    private val db = FirebaseFirestore.getInstance()
    private var productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadProducts()
        setupSearch()
        setListeners()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { product ->
            val bundle = Bundle().apply {
                putString("productID", product.ProductID)
            }
            findNavController().navigate(
                R.id.action_productsFragment_to_productDetailsFragment,
                bundle
            )
        }

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter
    }

    private fun loadProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (doc in result) {
                    val product = doc.toObject(Product::class.java)
                    product.ProductID = doc.id
                    productList.add(product)
                }
                adapter.submitList(productList.toList())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearch() {
        binding.svSearchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })
    }

    private fun filterProducts(query: String?) {
        val filtered = if (!query.isNullOrEmpty()) {
            productList.filter { it.name.contains(query, ignoreCase = true) }
        } else productList
        adapter.submitList(filtered)
    }

    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productsFragment_to_addProductFragment)
        }
    }
}
