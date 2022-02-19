package com.example.testing.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing.R
import com.example.testing.activities.CartActivity
import com.example.testing.activities.PlayerActivity
import com.example.testing.activities.RecipeDetailActivity
import com.example.testing.databinding.DialogCartQuantityBinding
import com.example.testing.databinding.RowCartBinding
import com.example.testing.models.ModelCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterCart : RecyclerView.Adapter<AdapterCart.HolderCart> {

    private var context: Context
    private var arrayListCart: ArrayList<ModelCart>

    private lateinit var binding: RowCartBinding

    constructor(context: Context, arrayListCart: ArrayList<ModelCart>) : super() {
        this.context = context
        this.arrayListCart = arrayListCart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCart {
        binding = RowCartBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderCart(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCart, position: Int) {
        val model = arrayListCart[position]
        loadProductDetails(model, holder)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("recipeId", model.productId)
            context.startActivity(intent)
        }

        holder.editBtn.setOnClickListener {
            cartQuantityDialog(model, holder)
        }
        holder.removeBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Delete")
                .setMessage("Remove from cart?")
                .setPositiveButton("DELETE") { d, e ->
                    removeCartItem(model, holder)
                }
                .setNegativeButton("NO") { d, e ->
                    d.dismiss()
                }
                .show()
        }
    }

    private fun removeCartItem(model: ModelCart, holder: HolderCart) {
        val firebaseAuth = FirebaseAuth.getInstance();
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .child("Cart")
            .child(model.cartId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to remove due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun cartQuantityDialog(model: ModelCart, holder: HolderCart) {

        val dialogCartBinding = DialogCartQuantityBinding.inflate(LayoutInflater.from(context))
        val titleTv = dialogCartBinding.titleTv
        val descriptionTv = dialogCartBinding.descriptionTv
        val unitPriceTv = dialogCartBinding.unitPriceTv
        val decrementBtn = dialogCartBinding.decrementBtn
        val quantityTv = dialogCartBinding.quantityTv
        val incrementBtn = dialogCartBinding.incrementBtn
        val totalPriceTv = dialogCartBinding.totalPriceTv
        val confirmCartBtn = dialogCartBinding.confirmCartBtn

        var prevQuantity = model.quantity
        titleTv.text = model.productTitle
        descriptionTv.text = model.productDescription
        unitPriceTv.text = model.price
        totalPriceTv.text = "${model.price.toDouble() * prevQuantity}"

        quantityTv.text = "$prevQuantity"

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogCartBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()

        incrementBtn.setOnClickListener {
            prevQuantity = prevQuantity + 1
            quantityTv.text = "$prevQuantity"
            totalPriceTv.text = "${model.price.toDouble() * prevQuantity}"
        }

        decrementBtn.setOnClickListener {
            if (prevQuantity > 1) {
                prevQuantity = prevQuantity - 1
                quantityTv.text = "$prevQuantity"

                totalPriceTv.text = "${model.price.toDouble() * prevQuantity}"
            }
        }

        confirmCartBtn.setOnClickListener {
            addToCart(model, holder, prevQuantity)
            alertDialog.dismiss()
        }
    }

    private fun addToCart(model: ModelCart, holder: HolderCart, prevQuantity: Int) {
        val firebaseAuth = FirebaseAuth.getInstance();
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["timestamp"] = timestamp
        hashMap["cartId"] = "${model.cartId}"
        hashMap["productId"] = "${model.cartId}"
        hashMap["quantity"] = prevQuantity

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Cart").child(model.cartId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Cart Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProductDetails(modelCart: ModelCart, holder: HolderCart) {
        val recipeId = modelCart.cartId
        val quantity = modelCart.quantity
        val ref = FirebaseDatabase.getInstance().getReference("Recipes")
        ref.child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = "${snapshot.child("categoryID").value}"
                    val description = "${snapshot.child("description").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val title = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val price = "${snapshot.child("price").value}"
                    val image = "${snapshot.child("image").value}"

                    modelCart.productTitle = title
                    modelCart.productDescription = description
                    modelCart.price = price
                    holder.titleTv.text = "$title"
                    holder.itemPriceEachTv.text = "$price"
                    holder.itemPriceTv.text = "${price.toDouble() * quantity}"
                    holder.itemQuantityTv.text = "[$quantity]"

                    if(!(context as Activity).isDestroyed)
                    Glide.with(context)
                        .load(image)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.productIv)

                    (context as CartActivity).calculateTotalPrice(price.toDouble(), quantity)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun getItemCount(): Int {
        return arrayListCart.size
    }

    inner class HolderCart(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productIv: ImageView = binding.productIv
        var titleTv: TextView = binding.titleTv
        var itemPriceTv: TextView = binding.itemPriceTv
        var itemPriceEachTv: TextView = binding.itemPriceEachTv
        var itemQuantityTv: TextView = binding.itemQuantityTv
        var editBtn: ImageButton = binding.editBtn
        var removeBtn: ImageButton = binding.removeBtn
    }


}