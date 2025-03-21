package com.example.mycloset.consts

enum class LoadingState {
    LOADING,
    NOT_LOADING
}

interface Listener<T> {
    fun onComplete(data: T)
}