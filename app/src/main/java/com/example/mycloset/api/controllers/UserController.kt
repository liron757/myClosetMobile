package com.example.mycloset.api.controllers

import androidx.lifecycle.LiveData
import com.example.mycloset.api.RequestExecutor.Companion.executor
import com.example.mycloset.api.RequestExecutor.Companion.localDb
import com.example.mycloset.api.firebase.services.UserService
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener

class UserController {
    private val userService = UserService()

    lateinit var otherUser: LiveData<User>
    lateinit var userPostsList: LiveData<List<Post>>

    fun getOtherUser(username: String): LiveData<User> {
        otherUser = localDb.userDao().getUserByUsername(username)
        return otherUser
    }

    fun getUserPosts(username: String): LiveData<List<Post>> {
        userPostsList = localDb.postDao().getUserPosts(username)
        return userPostsList
    }

    fun updateUser(user: User, listener: Listener<Void?>) {
        userService.updateUser(user, listener)
        refreshAllUsers()
    }

    fun updateLikedPosts(user: User) {
        userService.updateLikedPosts(user)
    }

    fun refreshAllUsers() {
        val localLastUpdated: Long = User.userLocalLastUpdate
        userService.getAllUsersSince(localLastUpdated,
            object : Listener<List<User>> {
                override fun onComplete(data: List<User>) {
                    executor.execute {
                        var time: Long = localLastUpdated
                        for (user in data) {
                            localDb.userDao().insert(user)
                            if (user.lastUpdated > time) {
                                time = user.lastUpdated
                            }
                        }
                        User.userLocalLastUpdate = time
                    }
                }
            })
    }

    fun isUsernameTaken(username: String, listener: Listener<Boolean>) {
        userService.isUsernameTaken(username, listener)
    }

    fun isEmailTaken(email: String, listener: Listener<Boolean>) {
        userService.isEmailTaken(email, listener)
    }

    companion object {
        val instance = UserController()
    }
}