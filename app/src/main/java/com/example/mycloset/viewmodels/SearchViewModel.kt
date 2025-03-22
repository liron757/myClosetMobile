package com.example.mycloset.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mycloset.api.models.post.Post

class SearchViewModel : ViewModel() {
    var data: List<Post> = listOf()
}
