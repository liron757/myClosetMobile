package com.example.mycloset.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.example.mycloset.api.controllers.CountryController
import com.example.mycloset.consts.Listener

object SpinnerAdapter {
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
