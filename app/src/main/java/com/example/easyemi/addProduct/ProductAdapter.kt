package com.example.easyemi.addProduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easyemi.R
import com.example.easyemi.data.models.Product
import com.example.easyemi.databinding.ItemProductBinding

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onBarcodeClick: ((Product) -> Unit)? = null, // optional
    private val showBarcodeIcon: Boolean = false // NEW flag
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback()) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "Price: ৳${product.price}"
            binding.tvProductStock.text = "Stock: ${product.quantity}"

            Glide.with(binding.imgProduct.context)
                .load(product.imageLink)
                .placeholder(R.drawable.ic_product_placeholder)
                .into(binding.imgProduct)

            // Regular item click
            binding.root.setOnClickListener { onItemClick(product) }

            // Barcode icon click using findViewById
            val barcodeIcon = binding.root.findViewById<ImageView>(R.id.imgBarcode)
            barcodeIcon?.apply {
                visibility = if (showBarcodeIcon) View.VISIBLE else View.GONE
                setOnClickListener {
                    onBarcodeClick?.invoke(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) =
            oldItem.ProductID == newItem.ProductID

        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
