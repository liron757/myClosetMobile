package com.example.mycloset.api.firebase.services

import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.api.models.post.LAST_UPDATED_KEY
import com.example.mycloset.api.models.post.POST_COLLECTION_KEY
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.consts.Listener
import com.google.firebase.Timestamp
import java.util.LinkedList

class PostService {
    private val model: FirebaseModel = FirebaseModel()

    private val firestore = model.firestore

    fun getAllPostsSince(since: Long, callback: Listener<List<Post>>) {
        firestore.collection(POST_COLLECTION_KEY)
            .whereGreaterThanOrEqualTo(LAST_UPDATED_KEY, Timestamp(since, 0))
            .get()
            .addOnCompleteListener { task ->
                val list: MutableList<Post> = LinkedList()
                if (task.isSuccessful) {
                    val jsonsList = task.result
                    for (json in jsonsList) {
                        val post: Post = Post.fromJson(json.data)
                        list.add(post)
                    }
                }
                callback.onComplete(list)
            }
    }

    fun addPost(post: Post, listener: Listener<Void?>) {
        firestore.collection(POST_COLLECTION_KEY).document(post.id).set(post.toJson())
            .addOnCompleteListener {
                listener.onComplete(null)
            }
    }

    fun deletePost(post: Post) {
        firestore.collection(POST_COLLECTION_KEY).document(post.id).delete()
    }

    fun updatePost(post: Post, listener: Listener<Void?>) {
        firestore.collection(POST_COLLECTION_KEY).document(post.id).update(post.toJson())
            .addOnSuccessListener {
                PostController.instance.refreshAllPosts()
                listener.onComplete(null)
            }
    }
}