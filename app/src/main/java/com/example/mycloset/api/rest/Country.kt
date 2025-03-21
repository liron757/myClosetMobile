package com.example.mycloset.api.rest

import com.google.gson.annotations.SerializedName

class Common {
    @SerializedName("common")
    lateinit var common: String
}

class Country {
    @SerializedName("name")
    lateinit var name: Common
}
