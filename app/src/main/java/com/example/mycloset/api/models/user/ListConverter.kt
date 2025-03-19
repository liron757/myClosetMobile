package com.example.mycloset.api.models.user

import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromLikedPostsList(likedPosts: List<String?>): String {
        val sb = StringBuilder()

        for (likedPost in likedPosts) {
            sb.append(likedPost)
            sb.append(",")
        }

        return sb.toString()
    }

    @TypeConverter
    fun toLikedPostsList(data: String): List<String> {
        return data.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
