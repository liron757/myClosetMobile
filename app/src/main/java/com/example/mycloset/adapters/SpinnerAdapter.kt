package com.example.mycloset.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.example.mycloset.api.controllers.CountryController
import com.example.mycloset.consts.Listener
import com.example.mycloset.consts.outfitCategories

object SpinnerAdapter {
    fun setOutfitCategoriesSpinner(context: Context): ArrayAdapter<String> {
        val outfitCategoriesSpinnerAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, outfitCategories)
        outfitCategoriesSpinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_dropdown_item_1line
        )
        return outfitCategoriesSpinnerAdapter
    }

    fun setCountrySpinner(context: Context, listener: Listener<ArrayAdapter<String>>) {
        CountryController.instance.getCountries(object : Listener<List<String>> {
            override fun onComplete(data: List<String>) {
                val countrySpinnerAdapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    data
                )
                countrySpinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                listener.onComplete(countrySpinnerAdapter)
            }
        })
    }
}
