package com.louis.bpaaisubmission.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.louis.bpaaisubmission.data.Repository
import com.louis.bpaaisubmission.data.remote.model.SessionModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun saveSession(session: SessionModel) {
        viewModelScope.launch {
            repository.UserPrefRepository().saveSession(session)
        }
    }

    fun loginUser(email: String, password: String) = repository.AuthRepository().loginUser(email, password).asLiveData()

}