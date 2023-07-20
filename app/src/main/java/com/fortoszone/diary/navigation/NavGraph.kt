package com.fortoszone.diary.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fortoszone.diary.presentation.components.DisplayAlertDialog
import com.fortoszone.diary.presentation.screens.auth.AuthenticationScreen
import com.fortoszone.diary.presentation.screens.auth.AuthenticationViewModel
import com.fortoszone.diary.presentation.screens.home.HomeScreen
import com.fortoszone.diary.util.Constants.APP_ID
import com.fortoszone.diary.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.home.route)
            }
        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.write.route)
            },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.authentification.route)
            }
        )
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit = {}
) {
    composable(route = Screen.authentification.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        val authenticated by viewModel.authenticated

        AuthenticationScreen(
            loadingState = loadingState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoadingState(true)
            },
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onDialogDismissed = {
                messageBarState.addSuccess(it)
            },
            onTokenIdReceived = {
                viewModel.login(
                    tokenId = it,
                    onSuccess = {
                        viewModel.setLoadingState(false)
                        navigateToHome()
                    },
                    onError = { error ->
                        messageBarState.addError(Exception(error))
                        viewModel.setLoadingState(false)
                    }
                )
            },
            authenticated = authenticated,
            navigateToHome = {
                navigateToHome()
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToAuth: () -> Unit
) {
    composable(route = Screen.home.route) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var onSignOutDialogOpened by remember {
            mutableStateOf(false)
        }
        HomeScreen(
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            onNavigateToWriteScreen = {
                navigateToWrite()
            },
            drawerState = drawerState,
            onSignOutClicked = {
                onSignOutDialogOpened = true
            },
        )

        DisplayAlertDialog(
            title = "Sign out",
            message = "Are you sure?",
            dialogOpened = onSignOutDialogOpened,
            onCloseDialog = { onSignOutDialogOpened = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            navigateToAuth()
                        }
                    }
                }
            })
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