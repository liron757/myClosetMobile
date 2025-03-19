package com.example.mycloset.consts

interface Listener<T> {
    fun onComplete(data: T)
}