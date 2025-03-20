package com.example.mycloset.api

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.mycloset.api.room.AppLocalDb
import com.example.mycloset.api.room.RoomDbRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RequestExecutor private constructor() {
    companion object {
        val executor: Executor = Executors.newSingleThreadExecutor()
        val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
        val localDb: RoomDbRepository = AppLocalDb.appDb
    }
}