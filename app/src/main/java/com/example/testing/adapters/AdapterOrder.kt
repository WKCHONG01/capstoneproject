package com.example.testing.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.MyApplication
import com.example.testing.activities.OrderDetailsActivity
import com.example.testing.databinding.RowOrderBinding
import com.example.testing.models.ModelOrder

class AdapterOrder : RecyclerView.Adapter<AdapterOrder.HolderOrder> {

    private var context: Context
    private var orderArrayList: ArrayList<ModelOrder>
    private lateinit var binding: RowOrderBinding

    constructor(context: Context, orderArrayList: ArrayList<ModelOrder>) {
        this.context = context
        this.orderArrayList = orderArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrder {
        binding = RowOrderBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderOrder(binding.root)
    }

    override fun onBindViewHolder(holder: HolderOrder, position: Int) {
        val model = orderArrayList[position]
        val orderByUid = model.orderByUid
        val orderId = model.orderId
        val orderStatus = model.orderStatus
        val orderCost = model.orderCost
        val orderTime = model.orderTime
        holder.orderIdTv.text = "OrderId: $orderId"
        holder.statusTv.text = "$orderStatus"
        holder.amountTv.text = "Total Amount: $orderCost"
        holder.dateTv.text = "${MyApplication.formatTimeStamp(orderTime)}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", "${model.orderId}")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return orderArrayList.size
    }

    inner class HolderOrder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderIdTv: TextView = binding.orderIdTv
        var dateTv: TextView = binding.dateTv
        var amountTv: TextView = binding.amountTv
        var statusTv: TextView = binding.statusTv
    }

}