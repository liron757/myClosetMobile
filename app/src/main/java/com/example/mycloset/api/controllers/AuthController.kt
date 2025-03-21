package com.example.mycloset.api.controllers

import androidx.lifecycle.LiveData
import com.example.mycloset.api.RequestExecutor.Companion.localDb
import com.example.mycloset.api.firebase.services.AuthService
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener

class AuthController {
    private val authService = AuthService()

    private var user: LiveData<User>? = null
    lateinit var username: String

    fun signUp(newUser: User, password: String, listener: Listener<Void?>) {
        authService.signUp(newUser, password, listener)
        UserController.instance.refreshAllUsers()
    }

    fun logIn(username: String, password: String, listener: Listener<Boolean>) {
        authService.logIn(username, password, listener)
    }

    val loggedUser: LiveData<User>
        get() {
            val username = authService.loggedUserUsername
            if (username != null) {
                this.username = username
            }
            if (user == null || user!!.value?.userName != username) {
                user = localDb.userDao().getUserByUsername(this.username)
                UserController.instance.refreshAllUsers()
            }
            return user as LiveData<User>
        }

    val isLoggedIn: Boolean
        get() = authService.isLoggedIn

    fun logOut() {
        authService.logOut()
        user = null
    }

    companion object {
        val instance = AuthController()
    }
}