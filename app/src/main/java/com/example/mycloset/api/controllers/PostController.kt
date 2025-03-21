package com.example.mycloset.api.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mycloset.api.firebase.services.PostService
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.RequestExecutor.Companion.executor
import com.example.mycloset.api.RequestExecutor.Companion.localDb
import com.example.mycloset.api.RequestExecutor.Companion.mainHandler
import com.example.mycloset.consts.Listener
import com.example.mycloset.consts.LoadingState

class PostController {
    private val postService = PostService()

    val eventPostsListLoadingState = MutableLiveData(LoadingState.NOT_LOADING)
    private var postList: LiveData<List<Post>>? = null
    var post: LiveData<Post>? = null

    val allPosts: LiveData<List<Post>>
        get() {
            if (postList == null) {
                postList = localDb.postDao().all
                refreshAllPosts()
            }
            return postList as LiveData<List<Post>>
        }

    fun getPostById(id: String): LiveData<Post> {
        post = localDb.postDao().getPostById(id)
        return post as LiveData<Post>
    }

    fun refreshAllPosts() {
        eventPostsListLoadingState.value = LoadingState.LOADING
        val localLastUpdated: Long = Post.postLocalLastUpdate
        postService.getAllPostsSince(localLastUpdated,
            object : Listener<List<Post>> {
                override fun onComplete(data: List<Post>) {
                    executor.execute {
                        helperFunc(localLastUpdated, data)
                        eventPostsListLoadingState.postValue(LoadingState.NOT_LOADING)
                    }
                }
            }
        )
    }

    fun helperFunc(localLastUpdated: Long, posts: List<Post>) {
        var time = localLastUpdated
        for (post in posts) {
            localDb.postDao().insert(post)
            if (post.lastUpdated > time) {
                time = post.lastUpdated
            }
        }
        Post.postLocalLastUpdate = time
    }

    fun addPost(post: Post, listener: Listener<Void?>) {
        postService.addPost(post, object : Listener<Void?> {
            override fun onComplete(data: Void?) {
                refreshAllPosts()
                listener.onComplete(data)
            }
        })
    }

    fun updatePost(post: Post, listener: Listener<Void?>) {
        postService.updatePost(post, listener)
        refreshAllPosts()
    }

    fun deletePost(post: Post, listener: Listener<Void?>) {
        postService.deletePost(post)
        executor.execute {
            localDb.postDao().delete(post)
            mainHandler.post { listener.onComplete(null) }
        }
        refreshAllPosts()
    }

    fun getLikedPosts(likedPosts: List<String>, listener: Listener<List<Post>>) {
        executor.execute {
            val data = localDb.postDao().getLikedPosts(likedPosts)
            mainHandler.post { listener.onComplete(data) }
        }
    }

    fun getPostsByOutfitName(outfitName: String, listener: Listener<List<Post>>) {
        refreshAllPosts()
        executor.execute {
            val data =
                localDb.postDao().getPostsByOutfitName(outfitName)
            mainHandler.post { listener.onComplete(data) }
        }
    }

    companion object {
        val instance = PostController()
    }
}
