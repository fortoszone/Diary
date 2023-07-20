package com.fortoszone.diary.presentation.screens.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.fortoszone.diary.util.Constants.CLIENT_ID
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    oneTapState: OneTapSignInState,
    loadingState: Boolean,
    messageBarState: MessageBarState,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
    authenticated: Boolean,
    onButtonClicked: () -> Unit,
    navigateToHome: () -> Unit
) {
    Scaffold(
        modifier = Modifier,
        content = {
            ContentWithMessageBar(
                messageBarState = messageBarState,
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                AuthenticationContent(
                    loadingState = loadingState,
                    onButtonClicked = onButtonClicked
                )
            }
        }
    )

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = CLIENT_ID,
        onTokenIdReceived = {
            onTokenIdReceived(it)
        },
        onDialogDismissed = {
            Log.d("OneTapSignIn", "Dialog   dismissed")
            onDialogDismissed(it)
        }
    )

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateToHome()
        }
    }
}