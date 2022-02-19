package com.example.testing.filters

import android.widget.Filter
import com.example.testing.adapters.RecipeAdapter
import com.example.testing.models.RecipeModel

class FilterRecipe : Filter{

    var filterList : ArrayList<RecipeModel>
    var adapterRecipe : RecipeAdapter

    constructor(filterList: ArrayList<RecipeModel>, RecipeAdapter: RecipeAdapter) {
        this.filterList = filterList
        this.adapterRecipe = RecipeAdapter
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint : CharSequence? = constraint
        val results = FilterResults()
        if(constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<RecipeModel>()
            for(i in filterList.indices){
                if(filterList[i].title.lowercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterRecipe.recipeArrayList = results.values as ArrayList<RecipeModel>
        adapterRecipe.notifyDataSetChanged()
    }
}