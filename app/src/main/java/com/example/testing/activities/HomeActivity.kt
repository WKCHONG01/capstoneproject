package com.example.testing.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.testing.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var actionBar : ActionBar
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{
            actionBar = supportActionBar!!
            actionBar.title = "Profile"
        }catch(ignored : NullPointerException){

        }

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
        binding.categoryBtn.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.favoriteListBtn.setOnClickListener {
            startActivity(Intent(this, favoriteListActivity::class.java))
        }
    }

    private fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            val email = firebaseUser.email
            binding.profileBtn.visibility = View.VISIBLE
            binding.favoriteListBtn.visibility = View.VISIBLE
        }
        else{
            binding.profileBtn.visibility = View.GONE
            binding.favoriteListBtn.visibility = View.GONE
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}