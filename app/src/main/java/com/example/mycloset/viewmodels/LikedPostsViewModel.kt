package com.example.mycloset.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mycloset.api.models.post.Post

class LikedPostsViewModel : ViewModel() {
    var likedPosts: List<Post> = listOf()
}