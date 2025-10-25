package com.example.easyemi.data.repository

import android.net.Uri
import com.example.easyemi.core.Nodes
import com.example.easyemi.data.models.Product
import com.example.easyemi.data.service.ImageService
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storageRef: StorageReference
): ImageService {
    override fun uploadProductImage(productImageUri: Uri): UploadTask {
        val storage: StorageReference = storageRef.child(Nodes.PRODUCTS).child("PRD_${System.currentTimeMillis()}")
        return storage.putFile(productImageUri)
    }

    override fun uploadProduct(product: Product): Task<Void> {
        return db.collection(Nodes.PRODUCTS).document(product.ProductID).set(product)
    }

    override fun getProductByUserID(userID: String): Task<QuerySnapshot> {
        return db.collection(Nodes.PRODUCTS).whereEqualTo(Nodes.USERID, userID).get()

    }
}