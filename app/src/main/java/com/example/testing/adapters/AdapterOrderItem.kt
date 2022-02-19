package com.example.testing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing.R
import com.example.testing.activities.PlayerActivity
import com.example.testing.databinding.RowOrderItemBinding
import com.example.testing.models.ModelOrderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterOrderItem : RecyclerView.Adapter<AdapterOrderItem.HolderOrderItem> {

    private var context: Context
    public var recipeArrayList: ArrayList<ModelOrderItem>

    private lateinit var binding: RowOrderItemBinding

    constructor(context: Context, recipeArrayList: ArrayList<ModelOrderItem>) : super() {
        this.context = context
        this.recipeArrayList = recipeArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrderItem {
        binding = RowOrderItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderOrderItem(binding.root)
    }

    override fun onBindViewHolder(holder: HolderOrderItem, position: Int) {
        val model = recipeArrayList[position]
        val recipeId = model.pId
        val price = model.price
        val quantity = model.quantity
        val productDescription = model.productDescription
        val title = model.title
        loadOrderItemDetails(model, holder)
        holder.titleTv.text = title
        holder.itemQuantityTv.text = "[$quantity]"
        holder.itemPriceEachTv.text = "$price"
        holder.itemPriceTv.text = "${price.toDouble() * quantity}"
        /*holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("recipeId", recipeId)
            context.startActivity(intent)
        }*/
        holder.itemView.rootView.setOnClickListener{
            PlayerActivity.pipMode = 1
            sendIntent(recipeId)
        }
    }

    private fun loadOrderItemDetails(model: ModelOrderItem, holder: HolderOrderItem) {
        val recipeId = model.pId
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
                    Glide.with(context)
                        .load(image)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.productIv)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun getItemCount(): Int {
        return recipeArrayList.size
    }

    inner class HolderOrderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productIv = binding.productIv
        val titleTv = binding.titleTv
        val itemPriceTv = binding.itemPriceTv
        val itemPriceEachTv = binding.itemPriceEachTv
        val itemQuantityTv = binding.itemQuantityTv
    }

    private fun sendIntent(id:String?){
        PlayerActivity.id = id
        val mIntent = Intent(context, PlayerActivity()::class.java)
        context.startActivity(mIntent)
    }
}