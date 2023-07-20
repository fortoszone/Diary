package com.fortoszone.diary.model

import androidx.compose.ui.graphics.Color
import com.fortoszone.diary.R
import com.fortoszone.diary.ui.theme.AngryColor
import com.fortoszone.diary.ui.theme.AwfulColor
import com.fortoszone.diary.ui.theme.BoredColor
import com.fortoszone.diary.ui.theme.CalmColor
import com.fortoszone.diary.ui.theme.DepressedColor
import com.fortoszone.diary.ui.theme.DisappointedColor
import com.fortoszone.diary.ui.theme.HappyColor
import com.fortoszone.diary.ui.theme.HumorousColor
import com.fortoszone.diary.ui.theme.LonelyColor
import com.fortoszone.diary.ui.theme.MysteriousColor
import com.fortoszone.diary.ui.theme.NeutralColor
import com.fortoszone.diary.ui.theme.RomanticColor
import com.fortoszone.diary.ui.theme.ShamefulColor
import com.fortoszone.diary.ui.theme.SurprisedColor
import com.fortoszone.diary.ui.theme.SuspiciousColor
import com.fortoszone.diary.ui.theme.TenseColor

enum class Mood(
    val icon: Int,
    val contentColor: Color,
    val containerColor: Color
) {
    Neutral(
        icon = R.drawable.neutral,
        contentColor = Color.Black,
        containerColor = NeutralColor
    ),

    Happy(
        icon = R.drawable.happy,
        contentColor = Color.Black,
        containerColor = HappyColor
    ),

    Angry(
        icon = R.drawable.angry,
        contentColor = Color.White,
        containerColor = AngryColor
    ),

    Bored(
        icon = R.drawable.bored,
        contentColor = Color.Black,
        containerColor = BoredColor
    ),

    Calm(
        icon = R.drawable.calm,
        contentColor = Color.Black,
        containerColor = CalmColor
    ),

    Depressed(
        icon = R.drawable.depressed,
        contentColor = Color.Black,
        containerColor = DepressedColor
    ),

    Disappointed(
        icon = R.drawable.dissapointed,
        contentColor = Color.White,
        containerColor = DisappointedColor
    ),

    Shameful(
        icon = R.drawable.shameful,
        contentColor = Color.White,
        containerColor = ShamefulColor
    ),

    Humorous(
        icon = R.drawable.humorous,
        contentColor = Color.Black,
        containerColor = HumorousColor
    ),

    Suspicious(
        icon = R.drawable.suspicious,
        contentColor = Color.Black,
        containerColor = SuspiciousColor
    ),

    Surprised(
        icon = R.drawable.surprised,
        contentColor = Color.Black,
        containerColor = SurprisedColor
    ),

    Awful(
        icon = R.drawable.awful,
        contentColor = Color.White,
        containerColor = AwfulColor
    ),

    Mysterious(
        icon = R.drawable.mysterious,
        contentColor = Color.White,
        containerColor = MysteriousColor
    ),

    Lonely(
        icon = R.drawable.lonely,
        contentColor = Color.White,
        containerColor = LonelyColor
    ),

    Romantic(
        icon = R.drawable.romantic,
        contentColor = Color.White,
        containerColor = RomanticColor
    ),

    Tense(
        icon = R.drawable.tense,
        contentColor = Color.White,
        containerColor = TenseColor
    )
}