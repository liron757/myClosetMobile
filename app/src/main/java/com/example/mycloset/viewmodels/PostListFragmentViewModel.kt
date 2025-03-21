package com.example.mycloset.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.models.post.Post

class PostListFragmentViewModel : ViewModel() {
    val data: LiveData<List<Post>> = PostController.instance.allPosts
}
