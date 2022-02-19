package com.example.testing.filters

import android.widget.Filter
import com.example.testing.adapters.AdapterCategory
import com.example.testing.models.ModelCategory

class FilterCategory: Filter {

    private var filterList: ArrayList<ModelCategory>
    private var adapaterCategory: AdapterCategory

    constructor(filterList: ArrayList<ModelCategory>, adapaterCategory: AdapterCategory): super(){
        this.filterList = filterList
        this.adapaterCategory = adapaterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint =  constraint
        val results = FilterResults()
        if(constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            val filteredModels : ArrayList<ModelCategory> = ArrayList()
            for(i in 0 until filterList.size){
                if (filterList[i].category.uppercase().contains(constraint)){
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

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapaterCategory. categoryArrayList= results.values as ArrayList<ModelCategory>
        adapaterCategory.notifyDataSetChanged()

    }

}