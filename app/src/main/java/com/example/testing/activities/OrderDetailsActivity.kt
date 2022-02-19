package com.example.testing.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testing.MyApplication
import com.example.testing.adapters.AdapterOrderItem
import com.example.testing.databinding.ActivityOderDetailsBinding
import com.example.testing.models.ModelOrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsActivity : AppCompatActivity() {

    private var orderId = ""

    private lateinit var orderItemsArrayList: ArrayList<ModelOrderItem>
    private lateinit var adapterOrderItem: AdapterOrderItem

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityOderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getStringExtra("orderId").toString()
        firebaseAuth = FirebaseAuth.getInstance()

        loadOrderDetails()
        loadOrderItems()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadOrderItems() {
        orderItemsArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.child(orderId).child("Items")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderItemsArrayList.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelOrderItem::class.java)
                        orderItemsArrayList.add(model!!)
                    }
                    adapterOrderItem = AdapterOrderItem(this@OrderDetailsActivity, orderItemsArrayList)
                    binding.productsRv.adapter = adapterOrderItem

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadOrderDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.child(orderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderId = snapshot.child("orderId").value
                    val orderCost = snapshot.child("orderCost").value
                    val orderByUid = snapshot.child("orderByUid").value
                    val orderStatus = snapshot.child("orderStatus").value
                    val orderTime = snapshot.child("orderTime").value
                    binding.orderIdTv.text = "OrderId: $orderId"
                    binding.dateTv.text = "${MyApplication.formatTimeStamp(orderTime as Long)}"
                    binding.amountTv.text = "Total Amount: $orderCost"
                    binding.statusTv.text = "$orderStatus"
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}