package com.example.mycloset.api.room

import androidx.room.Room.databaseBuilder
import com.example.mycloset.activities.MyApplication

object AppLocalDb {
    val appDb: RoomDbRepository
        get() = databaseBuilder(
            MyApplication.applicationContext(),
            RoomDbRepository::class.java,
            "appDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()
}
