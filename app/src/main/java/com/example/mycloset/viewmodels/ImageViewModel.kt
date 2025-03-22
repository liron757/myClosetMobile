package com.example.mycloset.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {
    var bitmap: Bitmap? = null
        set (bitmap) {
            if (bitmap !== null) {
                url = null
            }

            field = bitmap
        }
    var url: Uri? = null
        set(url) {
            if (url !== null) {
                bitmap = null
            }
            field = url
        }
}
