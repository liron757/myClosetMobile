package com.example.mycloset.api.models.post

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post)

    @Delete
    fun delete(post: Post)

    @get:Query("select * from Post")
    val all: LiveData<List<Post>>

    @Query("select * from Post where id=:id")
    fun getPostById(id: String): LiveData<Post>

    @Query("select * from Post where userName = :username")
    fun getUserPosts(username: String): LiveData<List<Post>>

    @Query("select * from Post where userName = :postUserName")
    fun getPostByUserName(postUserName: String): Post

    @Query("select * from Post where id in (:postIds)")
    fun getLikedPosts(postIds: List<String>): List<Post>

    @Query("SELECT * FROM Post WHERE outfitName LIKE '%' || :searchString || '%'")
    fun getPostsByOutfitName(searchString: String): List<Post>
}
