package com.fortoszone.diary.data.repository

import com.fortoszone.diary.model.Diary
import com.fortoszone.diary.util.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<Diaries>
}