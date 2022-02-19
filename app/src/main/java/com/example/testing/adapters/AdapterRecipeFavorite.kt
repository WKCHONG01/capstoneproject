package com.example.testing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.MyApplication
import com.example.testing.activities.PlayerActivity
import com.example.testing.databinding.RowRecipeFavoriteBinding
import com.example.testing.models.RecipeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AdapterRecipeFavorite: RecyclerView.Adapter<AdapterRecipeFavorite.HolderRecipeFavorite> {

    private val context: Context
    private var recipeArrayList: ArrayList<RecipeModel>
    private lateinit var binding: RowRecipeFavoriteBinding

    constructor(context: Context, recipeArrayList: ArrayList<RecipeModel>) {
        this.context = context
        this.recipeArrayList = recipeArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecipeFavorite {
        binding = RowRecipeFavoriteBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderRecipeFavorite(binding.root)
    }

    override fun onBindViewHolder(holder: HolderRecipeFavorite, position: Int) {
        val model = recipeArrayList[position]
        loadRecipeDetails(model,holder)
        /*holder.itemView.setOnClickListener {
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("recipeId",model.id)
            context.startActivity(intent)
        }*/
        holder.itemView.rootView.setOnClickListener{
            PlayerActivity.pipMode = 1
            sendIntent(model.id)
        }

        holder.removeFavBtn.setOnClickListener {
            MyApplication.removeFromFavorite(context,model.id)
        }
    }

    private fun loadRecipeDetails(model: RecipeModel, holder: AdapterRecipeFavorite.HolderRecipeFavorite) {
        val recipeId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Recipes")
        ref.child(recipeId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryID = "${snapshot.child("categoryID").value}"
                    val description = "${snapshot.child("description").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val title = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val image = "${snapshot.child("image").value}"
                    model.isFavorite = true
                    model.title = title
                    model.description = description
                    model.categoryId = categoryID
                    model.timestamp = timestamp.toLong()
                    model.uid = uid
                    model.image = image
                    Picasso.get().load(image).resize(500, 500).into(holder.recipeFavoriteIv)
                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    MyApplication.loadCategory("$categoryID", holder.categoryTv)
                    holder.titleTv.text = title
                    holder.descriptionTv.text = description
                    holder.dateTv.text = date
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun getItemCount(): Int {
        return recipeArrayList.size
    }

    inner class HolderRecipeFavorite(itemView: View): RecyclerView.ViewHolder(itemView){
        var titleTv = binding.titleTv
        var removeFavBtn = binding.removeFavBtn
        var descriptionTv = binding.descriptionTv
        var categoryTv = binding.categoryTv
        var dateTv = binding.dateTv
        var recipeFavoriteIv = binding.recipeFavoriteIv
    }

    private fun sendIntent(id:String?){
        PlayerActivity.id = id
        val mIntent = Intent(context, PlayerActivity()::class.java)
        context.startActivity(mIntent)
    }
}