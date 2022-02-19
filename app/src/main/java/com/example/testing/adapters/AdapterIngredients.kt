package com.example.testing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.R
import kotlinx.android.synthetic.main.recipedescription.view.*

class AdapterIngredients(
    private var context: Context,
    private var amountlist: ArrayList<String>?,
    private var ingredientslist:ArrayList<String>?): RecyclerView.Adapter<AdapterIngredients.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipedescription,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val ingredientitem = ingredientslist!![position]
        val amountitem = amountlist!![position]
        holder.itemView.ingredients.text = ingredientitem
        holder.itemView.ingredientsamount.text = amountitem
    }
    override fun getItemCount(): Int {
        return ingredientslist!!.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ingredient1: TextView = itemView.findViewById(R.id.ingredients)
        val amount1: TextView = itemView.findViewById(R.id.ingredientsamount)
    }
}