package com.example.testing.models

class RecipeModel {

    var uid: String = ""
    var id: String = ""
    var title: String = ""
    var description: String = ""
    var categoryId: String = ""
    var timestamp:Long = 0
    var isFavorite = false
    var price: Long = 0
    var image: String = ""
    var url:String? = null
    var viewCounts: Long = 0
    var ingredients: String = ""
    var instruction: String = ""

    constructor()

    constructor(uid: String, id: String, title: String, categoryId: String, timestamp: Long, description : String, isFavorite : Boolean, price : Long, image : String, viewCounts : Long, instruction : String, ingredients : String, url:String?) {
        this.uid = uid
        this.id = id
        this.title = title
        this.categoryId = categoryId
        this.timestamp = timestamp
        this.description = description
        this.url = url
        this.isFavorite = isFavorite
        this.price = price
        this.image = image
        this.viewCounts = viewCounts
        this.ingredients = ingredients
        this.instruction = instruction
    }
}