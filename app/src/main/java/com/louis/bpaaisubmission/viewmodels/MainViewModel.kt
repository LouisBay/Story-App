package com.louis.bpaaisubmission.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.louis.bpaaisubmission.data.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun deleteSession() {
        viewModelScope.launch {
            repository.UserPrefRepository().deleteSession()
        }
    }

    fun getSession() = repository.UserPrefRepository().getSession().asLiveData()

    fun getAllStories(token: String) = repository.StoryRepository().getAllStories(token).asLiveData()
}