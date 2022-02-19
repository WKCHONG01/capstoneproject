package com.example.testing.models

class ModelOrderItem {

    var pId: String = ""
    var price: String = ""
    var productDescription: String = ""
    var title: String = ""
    var quantity: Int = 0

    constructor()

    constructor(
        pId: String,
        price: String,
        productDescription: String,
        title: String,
        quantity: Int
    ) {
        this.pId = pId
        this.price = price
        this.productDescription = productDescription
        this.title = title
        this.quantity = quantity
    }


}