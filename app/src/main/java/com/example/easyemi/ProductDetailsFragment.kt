package com.example.easyemi

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.easyemi.data.models.Product
import com.example.easyemi.databinding.FragmentProductDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    private var productID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        productID = arguments?.getString("productID")

        if (productID != null) {
            loadProductDetails(productID!!)
        } else {
            Toast.makeText(requireContext(), "No product selected", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        setupListeners()
        return binding.root
    }

    private fun loadProductDetails(id: String) {
        db.collection("products").document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Product::class.java)
                    product?.let {
                        binding.tvProductNameDetail.text = it.name
                        binding.tvProductPriceDetail.text = "Price: ৳${it.price}"
                        binding.tvProductStockDetail.text = "Stock: ${it.quantity}"
                        binding.tvProductDescriptionDetail.text = it.description

                        Glide.with(requireContext())
                            .load(it.imageLink)
                            .placeholder(R.drawable.ic_product_placeholder)
                            .into(binding.imgProductDetail)
                    }
                } else {
                    Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load product details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupListeners() {
        // Edit button
        binding.btnEditProductDetail.setOnClickListener {
            val bundle = Bundle().apply {
                putString("productID", productID)
            }
            findNavController().navigate(R.id.action_productDetailsFragment_to_updateProductFragment, bundle)
        }

        // Delete button with confirmation dialog
        binding.btnBuyProductDetail.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { _, _ ->
                deleteProduct()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct() {
        productID?.let { id ->
            db.collection("products").document(id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
        }
    }
}