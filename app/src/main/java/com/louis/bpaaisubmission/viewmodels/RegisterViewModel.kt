package com.louis.bpaaisubmission.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.louis.bpaaisubmission.data.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String) =
        repository.AuthRepository().registerUser(name, email, password).asLiveData()
}