package com.example.testing.adapters

import android.content.Context
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.example.testing.filters.FilterRecipe
import com.example.testing.MyApplication
import com.example.testing.R
import com.example.testing.activities.PlayerActivity
import com.example.testing.activities.RecipeDetailActivity
import com.example.testing.databinding.RowRecipeBinding
import com.example.testing.models.RecipeModel
import com.squareup.picasso.Picasso

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.HolderRecipe>, Filterable {

    private var context: Context
    private lateinit var binding: RowRecipeBinding
    public var recipeArrayList: ArrayList<RecipeModel>
    private val filterList: ArrayList<RecipeModel>
    var filter: FilterRecipe? = null

    constructor(context: Context, recipeArrayList: ArrayList<RecipeModel>) : super() {
        this.context = context
        this.recipeArrayList = recipeArrayList
        this.filterList = recipeArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecipe {
        binding = RowRecipeBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderRecipe(binding.root)
    }

    override fun onBindViewHolder(holder: HolderRecipe, position: Int) {
        val model = recipeArrayList[position]
        val recipeId = model.id
        val categoryId = model.categoryId
        val timestamp = model.timestamp
        val title = model.title
        val description = model.description
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        val image = model.image
        try {
            Picasso.get().load(image).resize(500, 500).into(holder.recipeIv)
        } catch (e: Exception) {
            holder.recipeIv.setImageResource(R.drawable.ic_image_gray)
        }
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate
        //MyApplication.loadCategory(categoryId, holder.categoryTv)
        /*holder.itemView.setOnClickListener {
            sendIntent(recipeId)
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipeId)
            context.startActivity(intent)
        }*/
        holder.itemView.rootView.setOnClickListener{
            PlayerActivity.pipMode = 1
            PlayerActivity.recipeId = recipeId
            sendIntent(recipeId)
        }
    }

    override fun getItemCount(): Int {
        return recipeArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterRecipe(filterList, this)
        }
        return filter as FilterRecipe
    }

    inner class HolderRecipe(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        //val categoryTv = binding.categoryTv
        val dateTv = binding.dateTv
        val recipeIv = binding.recipeIv
    }

    private fun sendIntent(id:String?){
        PlayerActivity.id = id
        val mIntent = Intent(context, PlayerActivity()::class.java)
        context.startActivity(mIntent)
    }
}