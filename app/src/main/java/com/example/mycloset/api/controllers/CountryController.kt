package com.example.mycloset.api.controllers

import android.util.Log
import com.example.mycloset.api.RequestExecutor.Companion.mainHandler
import com.example.mycloset.api.rest.Country
import com.example.mycloset.api.rest.CountryApiService
import com.example.mycloset.api.rest.RetrofitClientInstance
import com.example.mycloset.consts.Listener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountryController {

    fun getCountries(listener: Listener<List<String>>) {
        val data: MutableList<String> = ArrayList()
        data.add("Country")

        val service = RetrofitClientInstance.instance.create(
            CountryApiService::class.java
        )
        val call = service.getAllCountries

        call.enqueue(object : Callback<List<Country>> {
            override fun onResponse(
                call: Call<List<Country>>,
                response: Response<List<Country>>
            ) {
                val countries = response.body()
                if (countries != null) {
                    for (country in countries) {
                        data.add(country.name.common)
                    }
                    mainHandler.post { listener.onComplete(data) }
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Log.e("TAG", "Failed to fetch countries", t)
            }
        })
    }

    companion object {
        val instance = CountryController()
    }
}