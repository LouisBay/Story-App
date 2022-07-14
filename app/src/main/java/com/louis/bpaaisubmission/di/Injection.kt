package com.louis.bpaaisubmission.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.louis.bpaaisubmission.data.Repository
import com.louis.bpaaisubmission.data.local.room.StoryDatabase
import com.louis.bpaaisubmission.data.remote.retrofit.ApiConfig


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideRepository(context: Context) : Repository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        return Repository.getInstance(apiService, context.dataStore, database)
    }
}