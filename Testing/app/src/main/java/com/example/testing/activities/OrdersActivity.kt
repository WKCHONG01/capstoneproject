package com.example.testing.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.testing.R
import com.example.testing.adapters.AdapterCart
import com.example.testing.adapters.AdapterOrder
import com.example.testing.databinding.ActivityCartBinding
import com.example.testing.databinding.ActivityOrdersBinding
import com.example.testing.models.ModelCart
import com.example.testing.models.ModelOrder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding
    private lateinit var ordersArrayList: ArrayList<ModelOrder>
    private lateinit var adapterOrder: AdapterOrder
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        Toast.makeText(this, "${firebaseAuth.uid}", Toast.LENGTH_SHORT).show()
        loadOrders()
    }

    private fun loadOrders() {
        ordersArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.orderByChild("orderByUid").equalTo(firebaseAuth.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersArrayList.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelOrder::class.java)
                        ordersArrayList.add(model!!)
                    }
                    adapterOrder = AdapterOrder(this@OrdersActivity, ordersArrayList)
                    binding.ordersRv.adapter = adapterOrder
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}