package com.fortoszone.diary.data.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fortoszone.diary.data.database.entity.ImageToUpload

interface ImageToUploadDao {
    @Query("SELECT * FROM images_to_upload_table ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToUpload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToUpload(imageToUpload: ImageToUpload)

    @Query("DELETE FROM images_to_upload_table WHERE id = :imageId")
    suspend fun cleanupImage(imageId: Int)
}