package com.example.testing.models

class ModelCart {

    var cartId: String = ""
    var productId: String = ""
    var productTitle: String = ""
    var productDescription: String = ""
    var price: String = ""
    var quantity: Int = 0
    var timestamp: Long = 0

    constructor()
    constructor(
        cartId: String,
        productId: String,
        productTitle: String,
        productDescription: String,
        price: String,
        quantity: Int,
        timestamp: Long
    ) {
        this.cartId = cartId
        this.productId = productId
        this.productTitle = productTitle
        this.productDescription = productDescription
        this.price = price
        this.quantity = quantity
        this.timestamp = timestamp
    }


}