package com.fortoszone.diary.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fortoszone.diary.R
import com.fortoszone.diary.presentation.components.GoogleButton

@Composable
fun AuthenticationContent(
    loadingState: Boolean,
    onButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(10f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.book),
                    contentDescription = stringResource(R.string.google_logo)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.auth_title),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                )
                Text(
                    text = stringResource(R.string.auth_body),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                GoogleButton(
                    loadingState = loadingState,
                    onClick = onButtonClicked
                )
            }
        }
    }
}