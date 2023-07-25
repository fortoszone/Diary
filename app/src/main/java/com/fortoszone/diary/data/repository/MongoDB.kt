package com.fortoszone.diary.data.repository

import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import com.fortoszone.diary.model.Diary
import com.fortoszone.diary.util.Constants.APP_ID
import com.fortoszone.diary.util.RequestState
import com.fortoszone.diary.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.ZoneId

object MongoDB : MongoRepository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureRealm()
    }

    override fun configureRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>(query = "ownerId == $0", user.id),
                        name = "User's Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
            Log.d("MongoDB", "configureRealm: ${user.id}")
        }
    }

    override fun getAllDiaries(): Flow<Diaries> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }

            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }

        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0 AND _id == $1", user.id, diaryId).asFlow()
                    .map { result ->
                        RequestState.Success(data = result.list.first())
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun insertDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                try {
                    val addedDiary = copyToRealm(diary.apply { ownerId = user.id })
                    RequestState.Success(data = addedDiary)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun updateDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                val queriedDiary =
                    query<Diary>(query = "_id == $0", diary._id).first()
                        .find()

                if (queriedDiary != null) {
                    queriedDiary.title = diary.title
                    queriedDiary.description = diary.description
                    queriedDiary.date = diary.date
                    queriedDiary.mood = diary.mood
                    queriedDiary.images = diary.images
                    RequestState.Success(data = queriedDiary)
                } else {
                    RequestState.Error(Exception("Queried Diary not found"))
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteDiary(diaryId: ObjectId): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                try {
                    val diary =
                        query<Diary>(query = "_id == $0 AND ownerId == $1", diaryId, user.id).find()
                            .first()
                    delete(diary)
                    RequestState.Success(data = diary)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }

        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }
}