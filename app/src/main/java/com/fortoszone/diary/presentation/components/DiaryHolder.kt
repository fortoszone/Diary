package com.fortoszone.diary.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fortoszone.diary.model.Diary
import com.fortoszone.diary.model.Mood
import com.fortoszone.diary.ui.theme.Elevation
import com.fortoszone.diary.util.toInstant
import io.realm.kotlin.ext.realmListOf
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

@Composable
fun DiaryHolder(
    diary: Diary,
    onClick: (String) -> Unit,
) {
    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }
    var galleryOpened by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
            ) { onClick(diary._id.toHexString()) },
    ) {
        Spacer(modifier = Modifier.width(14.dp))

        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp),
            tonalElevation = Elevation.level1,
        ) {}
        Spacer(modifier = Modifier.width(20.dp))

        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {
                    componentHeight = with(localDensity) { it.size.height.toDp() }
                },
            tonalElevation = Elevation.level1
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DiaryHeader(
                    mood = diary.mood,
                    time = diary.date.toInstant()
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    text = diary.description,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    ),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )

                if (diary.images.isNotEmpty()) {
                    ShowGalleryButton(
                        galleryOpened = galleryOpened,
                        onClick = { galleryOpened = !galleryOpened }
                    )
                }

                AnimatedVisibility(
                    visible = galleryOpened,
                    enter = fadeIn() + expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                    ) {
                        Gallery(images = diary.images)
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun DiaryHeader(
    mood: String,
    time: Instant
) {
    val moodState by remember {
        mutableStateOf(Mood.valueOf(mood))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Mood.valueOf(mood).containerColor)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(18.dp),
                painter = painterResource(id = moodState.icon),
                contentDescription = "Mood icon"
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = moodState.name,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                ),
                color = moodState.contentColor
            )
        }

        Text(
            text = SimpleDateFormat("hh:mm a").format(Date.from(time)),
            color = moodState.contentColor,
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ),
        )
    }
}

@Composable
fun ShowGalleryButton(
    galleryOpened: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = if (galleryOpened) "Hide Gallery" else "Show Gallery",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        )
    }
}

@Composable
@Preview
fun DiaryHolderPreview() {
    DiaryHolder(
        diary = Diary().apply {
            mood = Mood.Happy.name
            title = "This is a sample title"
            description =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget aliquam ultricies, nisl nisl aliquam nisl, eget aliquam nisl nisl eget."
            images = realmListOf("", "")
        },

        onClick = {}
    )
}