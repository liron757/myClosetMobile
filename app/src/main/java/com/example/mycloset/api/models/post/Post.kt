package com.example.mycloset.api.models.post

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mycloset.activities.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.time.Instant

@Entity
class Post {
    @PrimaryKey
    lateinit var id: String
    lateinit var userName: String

    lateinit var outfitName: String
    lateinit var outfitDescription: String
    lateinit var outfitPieces: String
    lateinit var outfitUrl: String
    lateinit var outfitCategory: String

    var lastUpdated: Long = 0

    constructor()

    constructor(
        id: String,
        userName: String,
        outfitCategory: String,
        outfitName: String,
        outfitDescription: String,
        outfitPieces: String,
        outfitUrl: String,
    ) {
        this.id = id
        this.userName = userName

        this.outfitName = outfitName
        this.outfitDescription = outfitDescription
        this.outfitPieces = outfitPieces
        this.outfitUrl = outfitUrl
        this.outfitCategory = outfitCategory
    }

    fun toJson(): Map<String, Any> {
        val json: MutableMap<String, Any> = HashMap()

        json[ID_KEY] = id
        json[USER_NAME_KEY] = userName
        json[OUTFIT_NAME_KEY] = outfitName
        json[DESCRIPTION_KEY] = outfitDescription
        json[OUTFIT_PIECES_KEY] = outfitPieces
        json[OUTFIT_IMAGE_KEY] = outfitUrl
        json[CATEGORY_KEY] = outfitCategory
        json[LAST_UPDATED_KEY] = FieldValue.serverTimestamp()

        return json
    }

    companion object {
        fun fromJson(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as String
            val username = json[USER_NAME_KEY] as String
            val outfitName = json[OUTFIT_NAME_KEY] as String
            val description = json[DESCRIPTION_KEY] as String
            val pieces = json[OUTFIT_PIECES_KEY] as String
            val outfitImg = json[OUTFIT_IMAGE_KEY] as String
            val category = json[CATEGORY_KEY] as String

            val post = Post(
                id,
                username,
                category,
                outfitName,
                description,
                pieces,
                outfitImg
            )

            try {
                val time = json[LAST_UPDATED_KEY] as Timestamp?
                post.lastUpdated = time!!.seconds
            } catch (e: Exception) {
                post.lastUpdated = Instant.now().epochSecond
            }

            return post
        }

        var postLocalLastUpdate: Long
            get() {
                val sharedPef: SharedPreferences = MyApplication.applicationContext()
                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                return sharedPef.getLong(LOCAL_LAST_UPDATED_KEY, 0)
            }
            set(time) {
                MyApplication.applicationContext()
                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    .edit().putLong(LOCAL_LAST_UPDATED_KEY, time).apply()
            }
    }
}
