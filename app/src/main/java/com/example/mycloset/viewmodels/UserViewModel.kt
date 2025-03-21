package com.example.mycloset.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mycloset.api.controllers.AuthController
import com.example.mycloset.api.models.user.User

class UserViewModel : ViewModel() {
    var user: LiveData<User>? = null
        get() {
            field = AuthController.instance.loggedUser
            return field
        }
        private set
}
