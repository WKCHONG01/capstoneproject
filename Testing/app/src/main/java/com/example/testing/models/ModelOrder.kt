package com.example.testing.models

class ModelOrder {

    var orderId: String = ""
    var orderByUid: String = ""
    var orderStatus: String = ""
    var orderCost: Double = 0.0
    var orderTime: Long = 0

    constructor()

    constructor(
        orderId: String,
        orderByUid: String,
        orderStatus: String,
        orderCost: Double,
        orderTime: Long
    ) {
        this.orderId = orderId
        this.orderByUid = orderByUid
        this.orderStatus = orderStatus
        this.orderCost = orderCost
        this.orderTime = orderTime
    }


}