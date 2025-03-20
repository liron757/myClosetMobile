package com.example.mycloset.api.firebase.services

import com.example.mycloset.api.controllers.AuthController
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.api.models.user.USER_COLLECTION_KEY
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.google.firebase.auth.UserProfileChangeRequest

class AuthService {
    private val model: FirebaseModel = FirebaseModel()

    private val firestore = model.firestore
    private val mAuth = model.mAuth

    fun signUp(newUser: User, password: String, listener: Listener<Void?>) {
        firestore.collection(USER_COLLECTION_KEY).document(newUser.userName)
            .set(User.toJson(newUser))
            .addOnCompleteListener {
                mAuth.createUserWithEmailAndPassword(newUser.email, password)
                    .addOnCompleteListener {
                        val profile =
                            UserProfileChangeRequest.Builder().setDisplayName(newUser.userName)
                                .build()
                        mAuth.currentUser!!.updateProfile(profile)
                        AuthController.instance.username = newUser.userName
                        mAuth.signInWithEmailAndPassword(newUser.email, password)
                            .addOnCompleteListener {
                                listener.onComplete(null)
                                UserController.instance.refreshAllUsers()
                            }
                    }
            }
    }

    fun logIn(username: String, password: String, listener: Listener<Boolean>) {
        firestore.collection(USER_COLLECTION_KEY).document(username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val user: User =
                        User.fromJson(
                            task.result!!.data!!
                        )
                    mAuth.signInWithEmailAndPassword(user.email, password)
                        .addOnCompleteListener { op ->
                            listener.onComplete(
                                op.isSuccessful
                            )
                        }
                } else {
                    listener.onComplete(task.isSuccessful)
                }
            }
    }

    val isLoggedIn: Boolean
        get() {
            val currentUser = mAuth.currentUser
            return currentUser != null
        }

    val loggedUserUsername: String?
        get() {
            val username = mAuth.currentUser!!.displayName
            return username
        }

    fun logOut() {
        mAuth.signOut()
    }
}