package com.louis.bpaaisubmission.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.louis.bpaaisubmission.data.remote.model.SessionModel
import com.louis.bpaaisubmission.data.remote.retrofit.ApiService
import com.louis.bpaaisubmission.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository private constructor(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
) {

    inner class AuthRepository {
        fun loginUser(email: String, password: String) = flow {
            emit(Result.Loading)
            apiService.loginUser(email, password).let {
                if (it.error) emit(Result.Error(it.message))
                else emit(Result.Success(it.loginResult))
            }
        }.catch { emit(Result.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)

        fun registerUser(name: String, email: String, password: String) = flow {
            emit(Result.Loading)
            apiService.registerUser(name, email, password).let {
                if (it.error) emit(Result.Error(it.message))
                else emit(Result.Success(it))
            }
        }.catch { emit(Result.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    inner class StoryRepository {
        fun getAllStories(token: String) = flow {
            emit(Result.Loading)
            apiService.getAllStories(token).let {
                if (it.error) emit(Result.Error(it.message))
                else emit(Result.Success(it.listStory))
            }
        }.catch { emit(Result.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)

        fun uploadNewStory(token: String, description: RequestBody, photo: MultipartBody.Part) = flow {
            emit(Result.Loading)
            apiService.uploadNewStory(token, description, photo).let {
                if (it.error) emit(Result.Error(it.message))
                else emit(Result.Success(it))
            }
        }.catch { emit(Result.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    inner class UserPrefRepository {
        fun getSession(): Flow<SessionModel> {
            return dataStore.data.map { pref ->
                SessionModel(
                    pref[LOGIN_STATE] ?: false,
                    pref[USER_TOKEN] ?: ""
                )
            }
        }

        suspend fun saveSession(session: SessionModel) {
            dataStore.edit { pref ->
                pref[LOGIN_STATE] = session.isLogin
                pref[USER_TOKEN] = session.token
            }
        }

        suspend fun deleteSession() {
            dataStore.edit { pref ->
                pref[LOGIN_STATE] = false
                pref[USER_TOKEN] = ""
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        private val LOGIN_STATE = booleanPreferencesKey("state")
        private val USER_TOKEN = stringPreferencesKey("token")

        fun getInstance(
            apiService: ApiService,
            dataStore: DataStore<Preferences>
        ) : Repository {
            return INSTANCE ?: synchronized(this) {
                Repository(apiService, dataStore).also {
                    INSTANCE = it
                }
            }
        }
    }
}