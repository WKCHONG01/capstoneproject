package com.example.testing.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.testing.MyApplication
import com.example.testing.R
import com.example.testing.databinding.ActivityRecipeDetailBinding
import com.example.testing.databinding.DialogCartQuantityBinding
import com.example.testing.models.RecipeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var recipeId = ""
    private var recipeTitle = ""
    private var recipeDescription = ""
    private var recipePrice = ""
    private var quantity = 1

    companion object {
        const val TAG = "RECIPE_DETAILS_TAG"

    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var recipeArrayList: ArrayList<RecipeModel>
    private var isInMyFavorite = false
    private var isInMyCart = false

    private var prevQuantity = 1
    private var prevCartId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeId = intent.getStringExtra("recipeId")!!
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            checkIsFavorite()
        }

        //MyApplication.incrementRecipeViewCount(recipeId)
        loadRecipeDetails()
        checkIfInCart()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.favoriteBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show()
            } else {
                if (isInMyFavorite) {
                    MyApplication.removeFromFavorite(this, recipeId)
                } else {
                    addToFavorite()
                }
            }
        }

        binding.addToCartBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show()
            } else {
                cartQuantityDialog()
            }
        }
    }

    private fun loadRecipeDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Recipes")
        ref.child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = "${snapshot.child("categoryID").value}"
                    recipeDescription = "${snapshot.child("description").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    recipeTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    recipePrice = "${snapshot.child("price").value}"

                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    MyApplication.loadCategory(categoryId, binding.categoryTv)

                    binding.titleTv.text = recipeTitle
                    binding.descriptionTv.text = recipeDescription
                    binding.dateTv.text = date
                    binding.priceTv.text = recipePrice
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun checkIsFavorite() {
        Log.d(TAG, "checkIsFavorite: Checking if recipe is in fav or not")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if (isInMyFavorite) {
                        Log.d(TAG, "onDataChange: Available in favorite")
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_favorite_filled_white, 0, 0
                        )
                        binding.favoriteBtn.text = "Remove Favorite"
                    } else {
                        Log.d(TAG, "onDataChange: Not available in favorite")
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_favorite_border_white, 0, 0
                        )
                        binding.favoriteBtn.text = "Add Favorite"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun addToFavorite() {
        Log.d(TAG, "addToFavorite: Adding to fav")
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["RecipeId"] = recipeId
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(recipeId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "addToFavorite: Added to fav")
                Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "addToFavorite: Failed to add to fav due to ${e.message}")
                Toast.makeText(this, "Failed to add to fav due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun checkIfInCart() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Cart").child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyCart = snapshot.exists()
                    if (isInMyCart) {
                        prevQuantity = "${snapshot.child("quantity").value}".toInt()
                        prevCartId = "${snapshot.child("cartId").value}"

                        Log.d(TAG, "onDataChange: Exists")
                        Log.d(TAG, "onDataChange: $prevQuantity")
                        Log.d(TAG, "onDataChange: $prevCartId")
                    } else {
                        Log.d(TAG, "onDataChange: Not Exists")
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun cartQuantityDialog() {
        val dialogCartBinding = DialogCartQuantityBinding.inflate(layoutInflater)
        val titleTv = dialogCartBinding.titleTv
        val descriptionTv = dialogCartBinding.descriptionTv
        val unitPriceTv = dialogCartBinding.unitPriceTv
        val decrementBtn = dialogCartBinding.decrementBtn
        val quantityTv = dialogCartBinding.quantityTv
        val incrementBtn = dialogCartBinding.incrementBtn
        val totalPriceTv = dialogCartBinding.totalPriceTv
        val confirmCartBtn = dialogCartBinding.confirmCartBtn
        quantityTv.text = "$prevQuantity"
        titleTv.text = recipeTitle
        descriptionTv.text = recipeDescription
        unitPriceTv.text = recipePrice
        totalPriceTv.text = "${recipePrice.toDouble() * prevQuantity}"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogCartBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()

        incrementBtn.setOnClickListener {
            prevQuantity = prevQuantity + 1
            quantityTv.text = "$prevQuantity"

            totalPriceTv.text = "${recipePrice.toDouble() * prevQuantity}"
        }

        decrementBtn.setOnClickListener {
            if (prevQuantity > 1) {
                prevQuantity = prevQuantity - 1
                quantityTv.text = "$prevQuantity"

                totalPriceTv.text = "${recipePrice.toDouble() * prevQuantity}"
            }
        }
        confirmCartBtn.setOnClickListener {
            addToCart()
            alertDialog.dismiss()
        }
    }

    private fun addToCart() {
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["timestamp"] = timestamp
        hashMap["cartId"] = "$recipeId"
        hashMap["productId"] = "$recipeId"
        hashMap["quantity"] = prevQuantity
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Cart").child(recipeId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Cart Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




}