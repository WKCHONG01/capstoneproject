package com.example.testing.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testing.adapters.AdapterPopularRecipe
import com.example.testing.databinding.ActivityPopularRecipeBinding
import com.example.testing.models.RecipeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PopularRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPopularRecipeBinding
    private lateinit var recipeArrayList:ArrayList<RecipeModel>
    private lateinit var adapterPopularRecipe: AdapterPopularRecipe
    private companion object{
        const val TAG = "POPULAR_RECIPE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadMostViewRecipes("viewCounts")
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
    }

    private fun loadMostViewRecipes(orderBy : String){
        recipeArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Recipes")
        ref.orderByChild(orderBy).limitToLast(5)
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeArrayList.clear()
                for(ds in snapshot.children){

                  //  Toast.makeText(applicationContext,ds.toString(),Toast.LENGTH_LONG).show()
                    val model = ds.getValue(RecipeModel::class.java)
                    recipeArrayList.add(model!!)
                }
                adapterPopularRecipe = AdapterPopularRecipe(this@PopularRecipeActivity,recipeArrayList)
                binding.recipesRv.adapter = adapterPopularRecipe
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}