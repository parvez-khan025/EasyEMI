package com.example.easyemi.data.service

import android.net.Uri
import com.example.easyemi.data.models.Product
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask

interface ImageService {
    fun uploadProductImage(productImageUri: Uri) : UploadTask
    fun uploadProduct(product: Product): Task<Void>
    fun getProductByUserID(userID: String) : Task<QuerySnapshot>
}