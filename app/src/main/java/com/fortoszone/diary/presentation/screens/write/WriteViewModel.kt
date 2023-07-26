package com.fortoszone.diary.presentation.screens.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fortoszone.diary.data.database.ImageToDeleteDao
import com.fortoszone.diary.data.database.ImageToUploadDao
import com.fortoszone.diary.data.database.entity.ImageToDelete
import com.fortoszone.diary.data.database.entity.ImageToUpload
import com.fortoszone.diary.data.repository.MongoDB
import com.fortoszone.diary.model.Diary
import com.fortoszone.diary.model.GalleryImage
import com.fortoszone.diary.model.GalleryState
import com.fortoszone.diary.model.Mood
import com.fortoszone.diary.model.RequestState
import com.fortoszone.diary.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.fortoszone.diary.util.fetchImagesFromFirebase
import com.fortoszone.diary.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {
    val galleryState = GalleryState()
    var uiState by mutableStateOf(UIState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

    private fun fetchSelectedDiary() {
        if (uiState.selectedDiaryId != null) {
            viewModelScope.launch(Dispatchers.Main) {
                val diaryId = ObjectId.invoke(uiState.selectedDiaryId!!)
                MongoDB.getSelectedDiary(diaryId = diaryId)
                    .catch {
                        emit(RequestState.Error(Exception("Diary is already deleted.")))
                    }
                    .collect { diary ->
                        if (diary is RequestState.Success) {
                            setMood(mood = Mood.valueOf(diary.data.mood))
                            setTitle(title = diary.data.title)
                            setDescription(description = diary.data.description)
                            setSelectedDiary(diary = diary.data)

                            fetchImagesFromFirebase(
                                remoteImagePaths = diary.data.images,
                                onImageDownload = { downloadedImage ->
                                    galleryState.addImage(
                                        GalleryImage(
                                            image = downloadedImage,
                                            remoteImagePath = extractRemoteImagePath(
                                                fullImageUrl = downloadedImage.toString()
                                            )
                                        )
                                    )
                                },
                                onImageDownloadFailed = {

                                }
                            )
                        }
                    }
            }
        }
    }

    private fun deleteImagesFromFirebase(images: List<String>? = null) {
        val storage = FirebaseStorage.getInstance().reference
        if (images != null) {
            images.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToDeleteDao.addImageToDelete(
                                ImageToDelete(
                                    remoteImagePath = remotePath
                                )
                            )
                        }
                    }
            }
        } else {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToDeleteDao.addImageToDelete(
                                ImageToDelete(
                                    remoteImagePath = remotePath
                                )
                            )
                        }
                    }
            }
        }
    }

    private fun extractRemoteImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${FirebaseAuth.getInstance().currentUser?.uid}/$imageName"
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

    private fun setSelectedDiary(diary: Diary) {
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        uiState = uiState.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.insertDiary(diary = diary.apply {
            if (uiState.updatedDateTime != null) {
                date = uiState.updatedDateTime!!
            }
        })
        if (result is RequestState.Success) {
            uploadImagesToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            onError(result.error.message.toString())
        }
    }

    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.updateDiary(diary = diary.apply {
            _id = ObjectId(uiState.selectedDiaryId!!)
            date = if (uiState.updatedDateTime != null)
                uiState.updatedDateTime!!
            else
                uiState.selectedDiary!!.date
        })
        if (result is RequestState.Success) {
            uploadImagesToFirebase()
            deleteImagesFromFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            onError(result.error.message.toString())
        }
    }

    fun upsertDiary(diary: Diary, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedDiaryId != null) {
                updateDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            } else {
                insertDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            }

        }
    }

    fun deleteDiary(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedDiaryId != null) {
                when (val result =
                    MongoDB.deleteDiary(diaryId = ObjectId(uiState.selectedDiaryId!!))) {
                    is RequestState.Success -> {
                        withContext(Dispatchers.Main) {
                            uiState.selectedDiary?.let { deleteImagesFromFirebase(it.images) }
                            onSuccess()
                        }
                    }

                    is RequestState.Error -> {
                        withContext(Dispatchers.Main) {
                            onError(result.error.message.toString())
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val remoteImagePath =
            "images/${FirebaseAuth.getInstance().currentUser?.uid}/" + "${image.lastPathSegment}-${System.currentTimeMillis()}.${imageType}"

        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToUploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }
}

data class UIState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    var title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)