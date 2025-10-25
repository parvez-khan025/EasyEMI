package com.example.easyemi

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyemi.addProduct.BarcodePrintAdapter
import com.example.easyemi.addProduct.ProductAdapter
import com.example.easyemi.data.models.Product
import com.example.easyemi.databinding.FragmentBarcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class BarcodeFragment : Fragment() {

    private var _binding: FragmentBarcodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private val products = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentBarcodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProductAdapter(
            onItemClick = { product -> /* optional item click */ },
            onBarcodeClick = { product -> generateAndShowBarcode(product) },
            showBarcodeIcon = true // show the barcode icon
        )
        binding.rvProducts.adapter = adapter

        fetchProducts()
        setupSearch()
    }

    private fun fetchProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                products.clear()
                for (doc in result) {
                    val product = Product(
                        ProductID = doc.id,
                        name = doc.getString("name") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        quantity = (doc.getLong("quantity") ?: 0L).toInt(),
                        imageLink = doc.getString("imageLink") ?: ""
                    )
                    products.add(product)
                }
                adapter.submitList(products.toList())
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearch() {
        binding.svSearchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return true
            }
        })
    }

    private fun filterProducts(query: String) {
        val filtered = products.filter { it.name.contains(query, ignoreCase = true) }
        adapter.submitList(filtered)
    }

    private fun generateAndShowBarcode(product: Product) {
        val bitmap = generateBarcodeBitmap(product.ProductID)
        showBarcodeDialog(bitmap)
    }

    private fun generateBarcodeBitmap(data: String): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, 600, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun showBarcodeDialog(barcode: Bitmap) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_barcode)

        val imageView = dialog.findViewById<ImageView>(R.id.ivBarcode)
        val btnShare = dialog.findViewById<Button>(R.id.btnShare)
        val btnPrint = dialog.findViewById<Button>(R.id.btnPrint)
        val btnDownload = dialog.findViewById<Button>(R.id.btnDownload)

        imageView.setImageBitmap(barcode)

        btnShare.setOnClickListener {
            val uri = saveBitmapToCache(barcode, "barcode.png")
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            startActivity(Intent.createChooser(intent, "Share Barcode"))
        }

        btnPrint.setOnClickListener {
            val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as android.print.PrintManager
            val jobName = "Barcode_Print_${System.currentTimeMillis()}"
            val printAdapter = BarcodePrintAdapter(requireContext(), barcode)
            printManager.print(jobName, printAdapter, null)
        }

        btnDownload.setOnClickListener {
            saveBitmapToGallery(barcode)
            Toast.makeText(requireContext(), "Saved to gallery", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun saveBitmapToCache(bitmap: Bitmap, fileName: String) =
        FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            File(requireContext().cacheDir, "images").apply {
                mkdirs()
                FileOutputStream(File(this, fileName)).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }
        )

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "barcode_${System.currentTimeMillis()}.png"
        val fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = requireContext().contentResolver
            val contentValues = ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?.let { resolver.openOutputStream(it) }
        } else {
            FileOutputStream(File(Environment.getExternalStorageDirectory(), filename))
        }
        fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}