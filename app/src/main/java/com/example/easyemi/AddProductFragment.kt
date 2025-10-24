package com.example.easyemi

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.easyemi.base.BaseFragment
import com.example.easyemi.core.extract
import com.example.easyemi.data.models.Product
import com.example.easyemi.databinding.FragmentAddProductBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : BaseFragment<FragmentAddProductBinding>(
    FragmentAddProductBinding::inflate
) {
    private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>

    override fun setListener() {

        permissionRequest = getPermissionRequest()

        with(binding) {

            btnSelectImage.setOnClickListener {
                // Launch the permission request
                permissionRequest.launch(permissionList)
            }

            btnAddProduct.setOnClickListener {
                val productName = etProductName.extract()
                val productPrice = etProductPrice.extract()
                val productQuantity = etProductQuantity.extract()
                val productDescription = etProductDescription.extract()

                val product = Product(
                    name = productName,
                    price = productPrice.toDouble(),
                    quantity = productQuantity.toInt(),
                    description = productDescription
                )
                uploadProduct(product)
            }
        }
    }

    private fun getPermissionRequest(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = results.all { it.value } // check actual results
            if (allGranted) {
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProduct(product: Product) {
        // TODO: Upload logic here
    }

    override fun allObserver() { }

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

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK){
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                binding.imgProductPreview.setImageURI(fileUri)
            }else if (resultCode== ImagePicker.RESULT_ERROR){
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}