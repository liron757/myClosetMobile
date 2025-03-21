package com.example.mycloset.api.rest

import retrofit2.Call
import retrofit2.http.GET

interface CountryApiService {
    @get:GET("v3.1/all")
    val getAllCountries: Call<List<Country>>
}
