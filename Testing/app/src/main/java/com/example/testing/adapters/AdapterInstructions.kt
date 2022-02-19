package com.example.testing.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.R
import kotlinx.android.synthetic.main.recipeinstructions.view.*

class AdapterInstructions(
    private var context: Context,
    private var instructionslist: ArrayList<String>?): RecyclerView.Adapter<AdapterInstructions.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipeinstructions,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val instructionitem = instructionslist!![position]
        holder.itemView.instructions.text = instructionitem
    }

    override fun getItemCount(): Int {
        return instructionslist!!.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val instruction1: TextView = itemView.findViewById(R.id.instructions)
    }
}