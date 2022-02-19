package com.example.testing

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        fun formatTimeStamp(timestamp: Long): String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy",cal).toString()
        }
        fun loadCategory(categoryId: String, categoryTv : TextView){
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category = "${snapshot.child("category").value}"
                        categoryTv.text = category
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

        public fun removeFromFavorite(context: Context, recipeId:String){
            val TAG = "REMOVE_FAV_TAG"
            Log.d(TAG, "removeFromFavorite: Removing from fav")
            val firebaseAuth = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Favorites").child(recipeId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d(TAG, "removeFromFavorite: Removed from fav")
                    Toast.makeText(context, "Removed from favorite", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {e->
                    Log.d(TAG, "removeFromFavorite: Failed to remove from fav due to ${e.message}")
                    Toast.makeText(context, "Failed to remove from fav due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun incrementRecipeViewCount(recipeId: String){
            val ref =  FirebaseDatabase.getInstance().getReference("Recipes")
            ref.child(recipeId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var viewCounts = "${snapshot.child("viewCounts").value}"
                        if(viewCounts == "" || viewCounts == "null"){
                            viewCounts = "0";
                        }
                        val newViewCounts = viewCounts.toLong() + 1
                        val hashMap = HashMap<String, Any>()
                        hashMap["viewCounts"] = newViewCounts
                        val dbRef = FirebaseDatabase.getInstance().getReference("Recipes")
                       dbRef.child(recipeId).updateChildren(hashMap)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }



}