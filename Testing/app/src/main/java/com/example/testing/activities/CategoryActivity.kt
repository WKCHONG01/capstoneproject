package com.example.testing.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.testing.databinding.ActivityCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import com.example.testing.adapters.AdapterCategory
import com.example.testing.models.ModelCategory


class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var adapterCategory: AdapterCategory
    private lateinit var actionBar: ActionBar
    companion object{
        var activity: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        loadCategories()

        try {
            actionBar = supportActionBar!!
            actionBar.title = "Profile"
        } catch (ignored: NullPointerException) {
        }

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterCategory.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


        binding.popularRecipeIv.setOnClickListener{
            startActivity(Intent(this, PopularRecipeActivity::class.java))
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.favoriteListBtn.setOnClickListener {
            startActivity(Intent(this, favoriteListActivity::class.java))
        }
        binding.orderListBtn.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
    }

    private fun loadCategories() {
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                }
                adapterCategory = AdapterCategory(this@CategoryActivity, categoryArrayList)
                binding.categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val email = firebaseUser.email
            binding.profileBtn.visibility = View.VISIBLE
            binding.favoriteListBtn.visibility = View.VISIBLE
        } else {
            binding.profileBtn.visibility = View.GONE
            binding.favoriteListBtn.visibility = View.GONE
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onRestart() {
        super.onRestart()
        activity = PlayerActivity.activity
        if(activity == 2) {
            val activityClass: Class<*>?

            activityClass = try {
                val prefs: SharedPreferences = this.getSharedPreferences("X", MODE_PRIVATE)
                Class.forName(
                    prefs.getString("lastActivity", PlayerActivity::class.java.getName())
                )
            } catch (ex: ClassNotFoundException) {
                CategoryActivity::class.java
            }
            startActivity(Intent(this, activityClass))

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(activity == 2){
            val activityClass: Class<*>?

            activityClass = try {
                val prefs: SharedPreferences = this.getSharedPreferences("X", MODE_PRIVATE)
                Class.forName(
                    prefs.getString("lastActivity", PlayerActivity::class.java.getName())
                )
            } catch (ex: ClassNotFoundException) {
                CategoryActivity::class.java
            }
            startActivity(Intent(this, activityClass))
            PlayerActivity.destroy = true
        }
    }
}