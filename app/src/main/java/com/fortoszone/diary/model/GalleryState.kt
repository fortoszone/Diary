package com.fortoszone.diary.model

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
fun rememberGalleryState(): GalleryState {
    return remember { GalleryState() }
}

class GalleryState {
    val images = mutableStateListOf<GalleryImage>()
    val imagesToBeDeleted = mutableStateListOf<GalleryImage>()

    fun addImage(image: GalleryImage) {
        images.add(image)
    }

    fun removeImage(image: GalleryImage) {
        images.remove(image)
    }
}

data class GalleryImage(
    val image: Uri,
    val remoteImagePath: String = "",
)