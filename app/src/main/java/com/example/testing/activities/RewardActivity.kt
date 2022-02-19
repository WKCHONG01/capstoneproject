package com.example.testing.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.testing.R
import com.example.testing.databinding.ActivityRewardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RewardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.redeemBtn.setOnClickListener {
            startActivity(Intent(this, RedeemActivity::class.java))
        }
    }

    private fun loadUserInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val userRank = "${snapshot.child("userRank").value}"
                    val rewardPoints = "${snapshot.child("rewardPoints").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val uid = "${snapshot.child("uid").value}"

                    binding.emailTv.text = email
                    binding.userRankTv.text = userRank
                    binding.rewardPointsTv.text = rewardPoints

                    try{
                        Glide.with(this@RewardActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(binding.profileIv)
                    }catch(e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}