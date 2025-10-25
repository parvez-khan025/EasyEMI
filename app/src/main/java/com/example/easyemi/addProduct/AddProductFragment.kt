package com.example.easyemi.addProduct

import android.Manifest
import android.app.Activity
import com.example.easyemi.R
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.easyemi.base.BaseFragment
import com.example.easyemi.core.extract
import com.example.easyemi.data.models.Product
import com.example.easyemi.databinding.FragmentAddProductBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddProductFragment :
    BaseFragment<FragmentAddProductBinding>(FragmentAddProductBinding::inflate) {

    private val product: Product by lazy { Product() }
    private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>
    private var selectedImageUri: Uri? = null

    override fun setListener() {
        permissionRequest = getPermissionRequest()

        with(binding) {
            backButton.setOnClickListener {
                findNavController().navigate(R.id.action_addProductFragment_to_dashboardFragment)
            }
            btnSelectImage.setOnClickListener {
                permissionRequest.launch(permissionList)
            }

            btnAddProduct.setOnClickListener {
                val name = etProductName.extract()
                val price = etProductPrice.extract()
                val quantity = etProductQuantity.extract()
                val description = etProductDescription.extract()

                if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || description.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                FirebaseAuth.getInstance().currentUser?.let {
                    product.apply {
                        this.userID= it.uid
                        this.ProductID = UUID.randomUUID().toString()
                        this.name = name
                        this.price = price.toDoubleOrNull() ?: 0.0
                        this.quantity = quantity.toIntOrNull() ?: 0
                        this.description = description
                        this.imageLink = selectedImageUri.toString()
                    }
                }

                uploadProduct(product)
            }
        }
    }

    override fun allObserver() {
        // No observers in this case
    }

    private fun uploadProduct(product: Product) {
        val uri = selectedImageUri ?: return
        Toast.makeText(requireContext(), "Uploading product...", Toast.LENGTH_SHORT).show()

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("products/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                product.imageLink = downloadUrl.toString()
                saveProductToFirestore(product)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProductToFirestore(product: Product) {
        val db = FirebaseFirestore.getInstance()
        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Product added successfully!", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save product", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        with(binding) {
            etProductName.text?.clear()
            etProductPrice.text?.clear()
            etProductQuantity.text?.clear()
            etProductDescription.text?.clear()
            imgProductPreview.setImageResource(R.drawable.ic_product_placeholder)
        }
        selectedImageUri = null
    }

    private fun getPermissionRequest(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = results.all { it.value }
            if (allGranted) {
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForImageResult.launch(intent)
                    }
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    if (uri != null) {
                        selectedImageUri = uri
                        binding.imgProductPreview.setImageURI(uri)
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    companion object {
        private val permissionList = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}