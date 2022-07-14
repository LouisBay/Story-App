package com.louis.bpaaisubmission.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.louis.bpaaisubmission.data.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {

    fun getSession() = repository.UserPrefRepository().getSession().asLiveData()

    fun getStoriesWithLocation(token: String) = repository.StoryRepository().getStoriesWithLocation(token).asLiveData()
}