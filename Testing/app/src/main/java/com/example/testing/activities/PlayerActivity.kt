package com.example.testing.activities

import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.app.PictureInPictureUiState
import android.app.ProgressDialog
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.testing.databinding.RecipeDetails2Binding
import com.example.testing.databinding.RecipeDetails2smallBinding
import com.example.testing.databinding.RowVideo2Binding
import com.example.testing.models.RecipeModel
import com.example.testing.models.RecipeType
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.recipe_details2.*
import kotlinx.android.synthetic.main.row_video2.view.*
import java.lang.reflect.Parameter
import android.content.SharedPreferences
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.testing.MyApplication
import com.example.testing.R
import com.example.testing.adapters.AdapterIngredients
import com.example.testing.adapters.AdapterInstructions
import com.example.testing.adapters.AdapterVideo2
import com.example.testing.databinding.DialogCartQuantityBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.recipe_details2.view.*


class PlayerActivity() : AppCompatActivity() {

    private var recipevidArrayList: ArrayList<RecipeType> = ArrayList()
    private var recipeingredients: ArrayList<String> = ArrayList()
    private var recipeingredientsamount: ArrayList<String> = ArrayList()
    private var recipeinstructions: ArrayList<String> = ArrayList()
    private lateinit var adapterVideo2: AdapterVideo2
    private lateinit var adapterIngredients: AdapterIngredients
    private lateinit var adapterInstructions: AdapterInstructions
    private lateinit var dialog: MaterialAlertDialogBuilder
    private var onStopstatus: Boolean = false
    private var onPauseStatus: Boolean = true
    private var picturestatus: Boolean = false
    private var buttonclicked: Boolean = false
    private var ingred: String? = null
    private var amount: String? = null
    private var instruct: String? = null
    private var ingredlengthsize: Int = 0
    private var amountlengthsize: Int = 0
    private var instructlengthsize: Int = 0
    private var i:Int = 0
    private var startindex: Int = 0
    private var endindex: Int = 0
    companion object {
        var destroy: Boolean = false
        var activity = 0
        var pipMode: Int = 0
        var id: String? = null
        var recipeId = ""
        const val TAG = "RECIPE_DETAILS_TAG"
        var portrait: Boolean = true
    }
    private var isFullscreen: Boolean = true
    private lateinit var binding: RecipeDetails2Binding
    private var recipeTitle = ""
    private var recipeDescription = ""
    private var recipePrice = ""
    private var quantity = 1
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var recipeArrayList: ArrayList<RecipeModel>
    private var isInMyFavorite = false
    private var isInMyCart = false
    private var prevQuantity = 1
    private var prevCartId = ""

    private fun loadVideosFromFirebase() {

        val ref =
            FirebaseDatabase.getInstance("https://testing-16c76-default-rtdb.firebaseio.com")
                .getReference("Recipes")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(isDestroyed || isFinishing )
                    return

                for (ds in snapshot.children) {
                    val modelVideo = ds.getValue(RecipeType::class.java)
                    if (modelVideo != null) {
                        if (modelVideo.id == id) {
                            recipePrice=modelVideo.price.toString()
                            recipevidArrayList.add(modelVideo!!)
                        }
                    }
                }
                val linearLayoutManager = object: LinearLayoutManager(this@PlayerActivity){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                val linearLayoutManager2 = object: LinearLayoutManager(this@PlayerActivity){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                val linearLayoutManager3 = object: LinearLayoutManager(this@PlayerActivity){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                playervideoRv.layoutManager = linearLayoutManager
                adapterVideo2 = AdapterVideo2(this@PlayerActivity, recipevidArrayList)
                binding.playervideoRv.adapter = adapterVideo2

                details(recipevidArrayList)

                adapterIngredients = AdapterIngredients(this@PlayerActivity, recipeingredientsamount,recipeingredients)
                playeringred_content.layoutManager = linearLayoutManager2
                playeringred_content.adapter = adapterIngredients
                adapterInstructions = AdapterInstructions(this@PlayerActivity, recipeinstructions)
                playerins_content.layoutManager = linearLayoutManager3
                playerins_content.adapter = adapterInstructions

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun details(recipelist: ArrayList<RecipeType>){


        recipeingredients.clear()
        recipeingredientsamount.clear()
        recipeinstructions.clear()
        startindex = 0
        endindex = 0
        i = 0
        ingred = recipevidArrayList[0].ingredients
        ingredlengthsize = ingred!!.length

        while (i < ingredlengthsize){
            if (i == 0){
                startindex = i
                i+=1
            }
            else{
                if (ingred!!.get(i).toString() == "#") {
                    endindex = i
                    var ingredient1 = ingred!!.substring(startindex, endindex)
                    recipeingredients.add(ingredient1)
                    startindex = i+1
                    i += 1
                }
                else{

                    i += 1
                }
            }

        }
        startindex = 0
        endindex = 0
        i = 0
        amount = recipevidArrayList[0].amount
        amountlengthsize = amount!!.length
        while (i < amountlengthsize){
            if (i == 0){
                startindex = i
                i+=1
            }
            else{
                if (amount!!.get(i).toString() =="#") {
                    endindex = i-1
                    var amount1 = amount!!.substring(startindex, endindex)
                    recipeingredientsamount.add(amount1)
                    startindex = i+1
                    i += 1
                }
                else{
                    i += 1
                }
            }

        }
        startindex = 0
        endindex = 0
        i = 0
        instruct = recipevidArrayList[0].instructions
        instructlengthsize = instruct!!.length

        while (i < instructlengthsize){
            if (i == 0){
                startindex = i
                i+=1
            }
            else{
                if (instruct!!.get(i).toString() == "#") {
                    endindex = i
                    var instruction1 = instruct!!.substring(startindex, endindex)
                    recipeinstructions.add(instruction1)
                    startindex = i+1
                    i += 1
                }
                else{
                    i += 1
                }
            }

        }
    }

    private fun playInFullscreen(enable: Boolean){
        if (enable){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            var param: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            relative1.layoutParams = param
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let{controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            binding.fullscreenBtn.setImageResource(R.drawable.fullscreen_exit_24)
            portrait = false
        }
        else{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            var param: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 900)
            relative1.layoutParams = param
            binding.fullscreenBtn.setImageResource(R.drawable.fullscreen_icon)
            portrait = true
        }
    }

    override fun onPause() {
        super.onPause()
        if(onPauseStatus) {
            if (binding.root.playerView.player!!.isPlaying) {
                binding.root.playerView.player!!.pause()
                binding.root.playpauseBtn.setImageResource(R.drawable.play_icon)
            }
        }
        else{
            val prefs = getSharedPreferences("X", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("lastActivity", javaClass.name)
            editor.commit()
            onPauseStatus = true}
    }

    private fun initializeBinding2(){
        binding.showBtn.setOnClickListener(){
            binding.root.playerView.player!!.pause()
            binding.root.playpauseBtn.setImageResource(R.drawable.play_icon)
            val customDialog = LayoutInflater.from(this).inflate(R.layout.recipe_details2small, binding.root,false)
            val bindingRDS = RecipeDetails2smallBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog).create()
            val linearLayoutManager4 = object: LinearLayoutManager(this@PlayerActivity){
                override fun canScrollVertically(): Boolean {
                    return false
                }

                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
            val linearLayoutManager5 = object: LinearLayoutManager(this@PlayerActivity){
                override fun canScrollVertically(): Boolean {
                    return false
                }

                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
            bindingRDS.playeringredContent.layoutManager = linearLayoutManager4
            bindingRDS.playeringredContent.adapter = adapterIngredients
            bindingRDS.playerinsContent.layoutManager = linearLayoutManager5
            bindingRDS.playerinsContent.adapter = adapterInstructions

            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.show()
            dialog.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        binding.backBtn.setOnClickListener(){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val customDialog = LayoutInflater.from(this).inflate(R.layout.recipe_details2small, binding.root,false)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog).create()
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, binding.root).let{controller ->
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
            var param: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 900)
            relative1.layoutParams = param
            binding.fullscreenBtn.setImageResource(R.drawable.fullscreen_icon)
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName) ==
                        AppOpsManager.MODE_ALLOWED
            } else {
                false
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status) {
                    this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                    binding.root.playerView.hideController()
                    pipMode = 0
                    dialog.dismiss()
                    onPauseStatus = false

                } else {
                    val intent = Intent(
                        "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Feature Not Supported!", Toast.LENGTH_SHORT).show()
                }
            }



        binding.fullscreenBtn.setOnClickListener{
            if(isFullscreen){
                isFullscreen = false
                playInFullscreen(enable = false)
            }
            else{
                isFullscreen = true
                playInFullscreen(enable = true)
            }
        }

        binding.favoriteBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show()
            } else {
                if (isInMyFavorite) {
                    MyApplication.removeFromFavorite(this, recipeId)
                } else {
                    addToFavorite()
                }
            }
        }

        binding.addToCartBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show()
            } else {
                cartQuantityDialog()
            }
        }

    }
    override fun onRestart(){
        super.onRestart()
        activity = 0
        if(!portrait){
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let{controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        if(destroy){
            finish()
        }
    }
    override fun onStop() {
        super.onStop()
        if(buttonclicked != true) {

            onStopstatus = true
        }
        else{
            activity = 2
            buttonclicked = false}
    }
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        buttonclicked = true
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            var param: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            relative1.layoutParams = param
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let{controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            binding.fullscreenBtn.setImageResource(R.drawable.fullscreen_exit_24)
            portrait = false
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, binding.root).let{controller ->
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
            var param: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 900)
            relative1.layoutParams = param
            portrait = true
            binding.fullscreenBtn.setImageResource(R.drawable.fullscreen_icon)
        }
    }
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if(isInPictureInPictureMode){

            binding.backBtn.setImageResource(R.drawable.up_icon)
            pipMode = 0
        }
        else {

            if (onStopstatus){
                finish()

                onStopstatus = false
            }
            else {
                if(pipMode == 1) {
                    finish()
                    val intent = Intent(this, PlayerActivity::class.java)
                    startActivity(intent)
                }
                else{
                    binding.backBtn.setImageResource(R.drawable.down_icon)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecipeDetails2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            checkIsFavorite()
        }

        MyApplication.incrementRecipeViewCount(recipeId)
        loadRecipeDetails()
        loadVideosFromFirebase()
        checkIfInCart()
        initializeBinding2()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity = 0
        binding.root.playerView.player!!.release()
    }


    private fun checkIsFavorite() {
        Log.d(RecipeDetailActivity.TAG, "checkIsFavorite: Checking if recipe is in fav or not")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if (isInMyFavorite) {
                        Log.d(RecipeDetailActivity.TAG, "onDataChange: Available in favorite")
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_favorite_filled_white, 0, 0
                        )
                        binding.favoriteBtn.text = "Remove Favorite"
                    } else {
                        Log.d(RecipeDetailActivity.TAG, "onDataChange: Not available in favorite")
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_favorite_border_white, 0, 0
                        )
                        binding.favoriteBtn.text = "Add Favorite"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun addToFavorite() {
        Log.d(RecipeDetailActivity.TAG, "addToFavorite: Adding to fav")
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["RecipeId"] = recipeId
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(recipeId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(RecipeDetailActivity.TAG, "addToFavorite: Added to fav")
                Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d(RecipeDetailActivity.TAG, "addToFavorite: Failed to add to fav due to ${e.message}")
                Toast.makeText(this, "Failed to add to fav due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun checkIfInCart() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Cart").child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyCart = snapshot.exists()
                    if (isInMyCart) {
                        prevQuantity = "${snapshot.child("quantity").value}".toInt()
                        prevCartId = "${snapshot.child("cartId").value}"

                        Log.d(RecipeDetailActivity.TAG, "onDataChange: Exists")
                        Log.d(RecipeDetailActivity.TAG, "onDataChange: $prevQuantity")
                        Log.d(RecipeDetailActivity.TAG, "onDataChange: $prevCartId")
                    } else {
                        Log.d(RecipeDetailActivity.TAG, "onDataChange: Not Exists")
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun cartQuantityDialog() {
        val dialogCartBinding = DialogCartQuantityBinding.inflate(layoutInflater)
        val titleTv = dialogCartBinding.titleTv
        val descriptionTv = dialogCartBinding.descriptionTv
        val unitPriceTv = dialogCartBinding.unitPriceTv
        val decrementBtn = dialogCartBinding.decrementBtn
        val quantityTv = dialogCartBinding.quantityTv
        val incrementBtn = dialogCartBinding.incrementBtn
        val totalPriceTv = dialogCartBinding.totalPriceTv
        val confirmCartBtn = dialogCartBinding.confirmCartBtn

        quantityTv.text = "$prevQuantity"
        titleTv.text = recipeTitle
        descriptionTv.text = recipeDescription
        unitPriceTv.text = recipePrice
        totalPriceTv.text = "${recipePrice.toDouble() * prevQuantity}"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogCartBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()

        incrementBtn.setOnClickListener {
            prevQuantity = prevQuantity + 1
            quantityTv.text = "$prevQuantity"
            totalPriceTv.text = "${recipePrice.toDouble() * prevQuantity}"
        }

        decrementBtn.setOnClickListener {
            if (prevQuantity > 1) {
                prevQuantity = prevQuantity - 1
                quantityTv.text = "$prevQuantity"
                totalPriceTv.text = "${recipePrice.toDouble() * prevQuantity}"
            }
        }
        confirmCartBtn.setOnClickListener {
            addToCart()
            alertDialog.dismiss()
        }
    }

    private fun addToCart() {
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["timestamp"] = timestamp
        hashMap["cartId"] = "$recipeId"
        hashMap["productId"] = "$recipeId"
        hashMap["quantity"] = prevQuantity
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Cart").child(recipeId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Cart Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRecipeDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Recipes")
        ref.child(recipeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = "${snapshot.child("categoryID").value}"
                    recipeDescription = "${snapshot.child("description").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    recipeTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    recipePrice = "${snapshot.child("price").value}"

                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    //MyApplication.loadCategory(categoryId, binding.categoryTv)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}