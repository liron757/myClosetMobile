package com.example.mycloset.activities

import android.app.Application
import android.content.Context
import com.squareup.picasso.Picasso

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val builder = Picasso.Builder(this)
        val built = builder.build()
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}

