package com.example.testing.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.testing.MyApplication
import com.example.testing.adapters.RecipeAdapter
import com.example.testing.databinding.ActivityRecipeListBinding
import com.example.testing.models.RecipeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecipeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeListBinding
    companion object{
        const val TAG = "RECIPE_LIST_TAG"
        var id: String? = ""
        var buttonPresed = false
    }
    private var categoryId = ""
    private var category = ""
    private lateinit var recipeArrayList:ArrayList<RecipeModel>
    private lateinit var adapterRecipe: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        binding.subTitleTv.text = category
        loadRecipeList()

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                try{
                    adapterRecipe.filter!!.filter(s)
                }catch(e: Exception){
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun loadRecipeList(){
        recipeArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Recipes")
        ref.orderByChild("categoryID").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    recipeArrayList.clear()
                    for(ds in snapshot.children){
                        val model = ds.getValue(RecipeModel::class.java)
                        if (model != null){
                            recipeArrayList.add(model)
                            Log.d(TAG,"onDataChange: ${model.title} ${model.categoryId}")
                        }
                    }
                    adapterRecipe = RecipeAdapter(this@RecipeListActivity,recipeArrayList)
                    binding.recipesRv.adapter = adapterRecipe
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
    override fun onResume(){
        super.onResume()
        Toast.makeText(this,"resume",Toast.LENGTH_SHORT).show()


    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this,"ListResume", Toast.LENGTH_SHORT).show()
        if(buttonPresed){
            buttonPresed = false
            MyApplication.incrementRecipeViewCount(id!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"Destroy", Toast.LENGTH_SHORT).show()
        if(buttonPresed){
            buttonPresed = false
            MyApplication.incrementRecipeViewCount(id!!)
        }
    }
}