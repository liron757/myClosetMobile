package com.example.mycloset.api.rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ENDPOINT_BASE_URL = "https://restcountries.com/"

object RetrofitClientInstance {
    private var retrofit: Retrofit? = null

    val instance: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(ENDPOINT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
}

