package com.example.mycloset.api.models.user

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mycloset.activities.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.time.Instant

@Entity
@TypeConverters(ListConverter::class)
class User {
    @PrimaryKey
    lateinit var email: String
    lateinit var userName: String
    lateinit var avatar: String
    lateinit var country: String
    lateinit var likedPosts: List<String>
    var lastUpdated: Long = 0

    constructor()

    constructor(
        userName: String,
        avatar: String,
        email: String,
        country: String,
        likedPosts: List<String>
    ) {
        this.userName = userName
        this.avatar = avatar
        this.email = email
        this.country = country
        this.likedPosts = likedPosts
    }

    companion object {
        fun fromJson(json: Map<String, Any>): User {
            val username = json[USER_NAME_KEY] as String
            val avatar = json[AVATAR_KEY] as String
            val email = json[EMAIL_KEY] as String
            val country = json[COUNTRY_KEY] as String
            val likedPosts = json[LIKED_POSTS_KEY] as List<String>

            val user = User(
                username,
                avatar,
                email,
                country,
                likedPosts
            )

            try {
                val time = json[LAST_UPDATED_KEY] as Timestamp?
                user.lastUpdated = time!!.seconds
            } catch (e: Exception) {
                user.lastUpdated = Instant.now().epochSecond
            }

            return user
        }

        fun toJson(user: User): Map<String, Any> {
            val json: MutableMap<String, Any> = HashMap()

            json[USER_NAME_KEY] = user.userName
            json[AVATAR_KEY] = user.avatar
            json[EMAIL_KEY] = user.email
            json[COUNTRY_KEY] = user.country
            json[LIKED_POSTS_KEY] = user.likedPosts
            json[LAST_UPDATED_KEY] = FieldValue.serverTimestamp()

            return json
        }

        var userLocalLastUpdate: Long
            get() {
                val sharedPef: SharedPreferences = MyApplication.applicationContext()
                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                return sharedPef.getLong(
                    LOCAL_LAST_UPDATED_KEY,
                    0
                )
            }
            set(time) {
                MyApplication.applicationContext()
                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    .edit()
                    .putLong(LOCAL_LAST_UPDATED_KEY, time)
                    .apply()
            }
    }
}
