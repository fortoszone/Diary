package com.fortoszone.diary.di

import android.content.Context
import androidx.room.Room
import com.fortoszone.diary.data.database.ImagesDatabase
import com.fortoszone.diary.util.Constants.IMAGES_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideImagesDatabase(@ApplicationContext context: Context): ImagesDatabase {
        return Room.databaseBuilder(
            context,
            ImagesDatabase::class.java,
            IMAGES_DATABASE
        ).build()
    }

    @Provides
    @Singleton
    fun provideImagesDao(imagesDatabase: ImagesDatabase) = imagesDatabase.imagesToUploadDao()
}