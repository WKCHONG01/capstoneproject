package com.example.testing.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testing.adapters.AdapterRecipeFavorite
import com.example.testing.databinding.ActivityFavoriteListBinding
import com.example.testing.models.RecipeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class favoriteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteListBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recipeArrayList: ArrayList<RecipeModel>
    private lateinit var adapterRecipeFavorite: AdapterRecipeFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        loadFavoriteRecipe()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadFavoriteRecipe(){
        recipeArrayList = ArrayList();
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    recipeArrayList.clear()
                    for(ds in snapshot.children){
                        val recipeId = "${ds.child("RecipeId").value}"
                        val recipeModel = RecipeModel()
                        recipeModel.id = recipeId
                        recipeArrayList.add(recipeModel)
                    }
                    adapterRecipeFavorite = AdapterRecipeFavorite(this@favoriteListActivity,recipeArrayList)
                    binding.favoriteRv.adapter = adapterRecipeFavorite
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}