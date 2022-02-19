package com.example.testing.models

class ModelCategory {

    var id : String  = ""
    var category: String = ""
    var timestamp: Long = 0
    var uid: String = ""
    var image: String = ""

    constructor()

    constructor(id: String, category: String, timestamp: Long, uid: String, image : String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
        this.image = image
    }
}