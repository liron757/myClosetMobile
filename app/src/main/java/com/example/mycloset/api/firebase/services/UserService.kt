package com.example.mycloset.api.firebase.services

import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.api.models.user.EMAIL_KEY
import com.example.mycloset.api.models.user.LAST_UPDATED_KEY
import com.example.mycloset.api.models.user.USER_COLLECTION_KEY
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot

class UserService {
    val model: FirebaseModel = FirebaseModel()
    private val firestore = model.firestore

    fun getAllUsersSince(since: Long, callback: Listener<List<User>>) {
        firestore.collection(USER_COLLECTION_KEY)
            .whereGreaterThan(LAST_UPDATED_KEY, Timestamp(since, 0))
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                var data: MutableList<User> = ArrayList()
                override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            data.add(User.fromJson(document.data))
                        }
                        callback.onComplete(data)
                    }
                }
            })
    }

    fun updateLikedPosts(user: User) {
        firestore.collection(USER_COLLECTION_KEY).document(user.userName)
            .update(User.toJson(user))
            .addOnSuccessListener { }
    }

    fun updateUser(user: User, listener: Listener<Void?>) {
        firestore.collection(USER_COLLECTION_KEY).document(user.userName)
            .update(User.toJson(user)).addOnSuccessListener {
                UserController.instance.refreshAllUsers()
                listener.onComplete(null)
            }
    }

    fun isUsernameTaken(username: String, listener: Listener<Boolean>) {
        firestore.collection(USER_COLLECTION_KEY).document(username).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    listener.onComplete(task.result.data != null)
                }
            }
    }

    fun isEmailTaken(email: String, listener: Listener<Boolean>) {
        firestore.collection(USER_COLLECTION_KEY).whereEqualTo(EMAIL_KEY, email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    listener.onComplete(task.result.documents.isNotEmpty())
                }
            }
    }
}