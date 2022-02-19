package com.example.testing.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.example.testing.MyApplication
import com.example.testing.R
import com.example.testing.databinding.ActivityLoginBinding
import com.example.testing.databinding.ActivityProfileBinding
import com.example.testing.databinding.ActivityRedeemBinding
import com.example.testing.databinding.ActivityRewardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RedeemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedeemBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedeemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        redeemRewardFirst()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.redeemFirstBtm.setOnClickListener {
            redeemRewardFirst()
        }

    }

    private fun redeemRewardFirst() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("rewardPoints")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    fun main(args: Array<String>) {
                        val rewardPoints = "${snapshot.child("rewardPoints").value}"
                        var first : Int = rewardPoints.toInt()
                        val redeemFirst : String = "500"
                        var second: Int = redeemFirst.toInt()

                        if(rewardPoints >= redeemFirst){
                            val result = first-second
                            Toast.makeText(this@RedeemActivity, "Redeem successfully", Toast.LENGTH_LONG)

                            val hashmap: HashMap<String, Any> = HashMap()
                            hashmap["rewardPoints"] = result

                            val reference = FirebaseDatabase.getInstance().getReference("Users")
                            reference.child(firebaseAuth.uid!!)
                                .updateChildren(hashmap)
                        }
                        else if(rewardPoints <= redeemFirst){
                            Toast.makeText(this@RedeemActivity, "Not enough reward points", Toast.LENGTH_LONG)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}


