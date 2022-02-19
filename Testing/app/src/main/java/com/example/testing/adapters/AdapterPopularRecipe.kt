package com.example.testing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.MyApplication
import com.example.testing.activities.PlayerActivity
import com.example.testing.activities.RecipeDetailActivity
import com.example.testing.databinding.RowPopularRecipeBinding
import com.example.testing.models.RecipeModel
import com.squareup.picasso.Picasso

class AdapterPopularRecipe: RecyclerView.Adapter<AdapterPopularRecipe.HolderPopularRecipe> {

    private var context : Context
    private lateinit var binding: RowPopularRecipeBinding
    private var recipeArrayList: ArrayList<RecipeModel>

    constructor(context: Context, recipeArrayList : ArrayList<RecipeModel>){
        this.context = context
        this.recipeArrayList = recipeArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPopularRecipe.HolderPopularRecipe {
        binding = RowPopularRecipeBinding.inflate(LayoutInflater.from(context),parent, false)
        return HolderPopularRecipe(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterPopularRecipe.HolderPopularRecipe, position: Int) {
        val model = recipeArrayList[position]
        val recipeId = model.id
        val categoryId = model.categoryId
        val timestamp = model.timestamp
        val title = model.title
        val description = model.description
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        val image = model.image
        val viewCounts = model.viewCounts
        Picasso.get().load(image).resize(500, 500).into(holder.recipeIv)
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate
        /*holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("recipeId",recipeId)
            context.startActivity(intent)
        }*/
        holder.itemView.rootView.setOnClickListener{
            PlayerActivity.pipMode = 1
            sendIntent(recipeId)
        }
    }

    override fun getItemCount(): Int {
        return recipeArrayList.size
    }

    inner class HolderPopularRecipe(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        //val categoryTv = binding.categoryTv
        val dateTv = binding.dateTv
        val recipeIv = binding.recipeIv
    }

    private fun sendIntent(id:String?){
        PlayerActivity.recipeId = id.toString()
        PlayerActivity.id = id
        val mIntent = Intent(context, PlayerActivity()::class.java)
        context.startActivity(mIntent)
    }
}