package com.fortoszone.diary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fortoszone.diary.presentation.screens.auth.AuthenticationScreen
import com.fortoszone.diary.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute()
        homeRoute()
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute() {
    composable(route = Screen.authentification.route) {
        AuthenticationScreen(
            loadingState = false,
            onButtonClicked = {

            }
        )
    }
}

fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.home.route) {

    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.write.route,
        arguments = listOf(
            navArgument(name = WRITE_SCREEN_ARGUMENT_KEY) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        )
    ) {

    }
}