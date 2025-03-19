package com.example.mycloset.api

import android.graphics.Bitmap
import com.example.mycloset.consts.Listener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.MemoryLruGcSettings
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class FirebaseModel internal constructor() {
    val firestore: FirebaseFirestore
    val mAuth: FirebaseAuth
    private var storage: FirebaseStorage

    init {
        val memoryCacheSettings = MemoryCacheSettings.newBuilder()
            .setGcSettings(
                MemoryLruGcSettings.newBuilder()
                    .setSizeBytes(0)
                    .build()
            )
            .build()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(memoryCacheSettings)
            .build()

        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        storage = FirebaseStorage.getInstance()

        mAuth = FirebaseAuth.getInstance()
    }

    fun uploadImage(name: String, bitmap: Bitmap, listener: Listener<String?>) {
        val storageRef = storage.reference
        val imagesRef =
            storageRef.child("images/$name.jpeg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            listener.onComplete(
                null
            )
        }.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                listener.onComplete(
                    uri.toString()
                )
            }
        }
    }

    companion object {
        val instance = FirebaseModel()
    }
}
