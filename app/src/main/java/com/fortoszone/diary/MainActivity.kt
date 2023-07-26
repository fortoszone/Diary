package com.fortoszone.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.fortoszone.diary.data.database.ImageToDeleteDao
import com.fortoszone.diary.data.database.ImageToUploadDao
import com.fortoszone.diary.navigation.Screen
import com.fortoszone.diary.navigation.SetupNavGraph
import com.fortoszone.diary.ui.theme.DiaryTheme
import com.fortoszone.diary.util.Constants.APP_ID
import com.fortoszone.diary.util.retryDeletingImageFromFirebase
import com.fortoszone.diary.util.retryUploadingImageToFirebase
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseApp.initializeApp(this)

        setContent {
            DiaryTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = {
                        keepSplashScreen = false
                    }
                )
            }
        }

        cleanupCheck(lifecycleScope, imageToUploadDao, imageToDeleteDao)
    }

    private fun cleanupCheck(
        scope: CoroutineScope,
        imageToUploadDao: ImageToUploadDao,
        imageToDeleteDao: ImageToDeleteDao
    ) {
        scope.launch(Dispatchers.IO) {
            val result = imageToUploadDao.getAllImages()
            result.forEach { imageToUpload ->
                retryUploadingImageToFirebase(
                    imageToUpload = imageToUpload,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imageToUploadDao.cleanupImage(imageId = imageToUpload.id)
                        }
                    }
                )
            }

            val result2 = imageToDeleteDao.getAllImages()
            result2.forEach { imageToDelete ->
                retryDeletingImageFromFirebase(
                    imageToDelete = imageToDelete,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imageToDeleteDao.cleanupImage(imageId = imageToDelete.id)
                        }
                    }
                )
            }
        }
    }

    private fun getStartDestination(): String {
        val user = App.create(APP_ID).currentUser
        return if (user != null && user.loggedIn) Screen.Home.route
        else Screen.Authentication.route
    }
}