package com.fortoszone.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.fortoszone.diary.data.database.ImageToUploadDao
import com.fortoszone.diary.navigation.Screen
import com.fortoszone.diary.navigation.SetupNavGraph
import com.fortoszone.diary.ui.theme.DiaryTheme
import com.fortoszone.diary.util.Constants.APP_ID
import com.google.firebase.FirebaseApp
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    var keepSplashScreen = true

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
    }

    private fun cleanupCheck(
        scope: CoroutineScope,
        imageToUploadDao: ImageToUploadDao
    ) {
        scope.launch(Dispatchers.IO) {
            val imagesToUpload = imageToUploadDao.getAllImages()
            imagesToUpload.forEach {
                imageToUploadDao.cleanupImage(it.id)
            }
        }
    }

    private fun getStartDestination(): String {
        val user = App.create(APP_ID).currentUser
        return if (user == null) {
            Screen.Authentication.route
        } else {
            Screen.Home.route
        }
    }
}