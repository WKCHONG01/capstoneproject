package com.example.testing.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testing.MyApplication
import com.example.testing.adapters.AdapterCart
import com.example.testing.databinding.ActivityCartBinding
import com.example.testing.models.ModelCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartArrayList: ArrayList<ModelCart>
    private lateinit var adapterCart: AdapterCart
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var totalPrice: Double = 0.0
    private var userAddress = ""
    private var userName = ""
    private var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")

        loadCart()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.placeOrderBtn.setOnClickListener {
            if (cartArrayList.size <= 0) {
                Toast.makeText(this, "No Items in cart", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Place Order")
                    .setMessage("Are you sure you want to place order?")
                    .setPositiveButton("CONFIRM") { d, e ->
                        placeOrder()
                    }
                    .setNegativeButton("CANCEL") { d, e ->
                        d.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val uid = "${snapshot.child("uid").value}"
                    userAddress = "${snapshot.child("address").value}"
                    userName = "${snapshot.child("name").value}"

                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadCart() {
        cartArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Cart")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartArrayList.clear()
                    totalPrice = 0.0
                    for (ds in snapshot.children) {
                        try {
                            val model = ds.getValue(ModelCart::class.java)
                            cartArrayList.add(model!!)
                        } catch (e: Exception) {
                            Toast.makeText(this@CartActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    adapterCart = AdapterCart(this@CartActivity, cartArrayList)
                    binding.cartRv.adapter = adapterCart
                    if(cartArrayList.size<=0){
                        binding.placeOrderBtn.text = "Place Order"
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    public fun calculateTotalPrice(itemPrice: Double, quantity: Int) {
        totalPrice = totalPrice + (itemPrice * quantity)

        binding.placeOrderBtn.text = "Place Order [$totalPrice]"
    }

    private fun placeOrder() {
        progressDialog.setMessage("Placing order...")
        progressDialog.show()
        val timestamp = System.currentTimeMillis();
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["orderId"] = "$timestamp"
        hashMap["orderTime"] = timestamp
        hashMap["orderStatus"] = "In Progress" //In Progress/Completed/Cancelled
        hashMap["orderCost"] = totalPrice
        hashMap["orderByUid"] = "${firebaseAuth.uid}"
        hashMap["address"] = "$userAddress"
        hashMap["userName"] = "$userName"
        hashMap["phoneNumber"] = "$phoneNumber"

        val ref = FirebaseDatabase.getInstance().getReference("Orders")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                for (i in 0 until cartArrayList.size) {
                    val pId: String = cartArrayList[i].productId
                    val price: String = cartArrayList[i].price
                    val title: String = cartArrayList[i].productTitle
                    val productDescription: String = cartArrayList[i].productDescription
                    val quantity: Int = cartArrayList[i].quantity
                    val hashMap1: HashMap<String, Any> = HashMap()
                    hashMap1["pId"] = pId
                    hashMap1["title"] = title
                    hashMap1["productDescription"] = productDescription
                    hashMap1["quantity"] = quantity
                    hashMap1["price"] = price
                    ref.child("$timestamp").child("Items").child(pId).setValue(hashMap1)
                }

                Handler().postDelayed({
                    val cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    cartRef.child("${firebaseAuth.uid}").child("Cart")
                        .removeValue()
                    progressDialog.dismiss()
                    Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
                }, 3000)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Failed to place order due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}