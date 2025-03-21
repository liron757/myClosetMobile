package com.example.mycloset.api.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.post.PostDao
import com.example.mycloset.api.models.user.User
import com.example.mycloset.api.models.user.UserDao

@Database(entities = [Post::class, User::class], version = 11)
abstract class RoomDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}