package com.fortoszone.diary.navigation

import com.fortoszone.diary.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

sealed class Screen(val route: String) {
    object authentification : Screen("authentification_screen")
    object home : Screen("home_screen")
    object write : Screen("write_screen?$WRITE_SCREEN_ARGUMENT_KEY=$WRITE_SCREEN_ARGUMENT_KEY") {
        fun passDiaryId(diaryId: String) = "write_screen?diaryId=$WRITE_SCREEN_ARGUMENT_KEY"
    }
}