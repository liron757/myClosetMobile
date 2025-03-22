package com.example.mycloset.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.models.post.Post

class UserProfileViewModel : ViewModel() {
    private var data: LiveData<List<Post>>? = null

    fun getUserPosts(username: String): LiveData<List<Post>>? {
        this.data = UserController.instance.getUserPosts(username)
        return data
    }
}
