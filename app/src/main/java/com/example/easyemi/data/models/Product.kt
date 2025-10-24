package com.example.easyemi.data.models

data class Product(
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var imageLink: String = "",
    var userID: String = "",
    var ProductID: String = ""
)
