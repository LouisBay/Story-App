package com.louis.bpaaisubmission.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.louis.bpaaisubmission.data.Repository
import com.louis.bpaaisubmission.di.Injection
import com.louis.bpaaisubmission.viewmodels.AddStoryViewModel
import com.louis.bpaaisubmission.viewmodels.LoginViewModel
import com.louis.bpaaisubmission.viewmodels.MainViewModel
import com.louis.bpaaisubmission.viewmodels.RegisterViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> { LoginViewModel(repository) as T }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> { MainViewModel(repository) as T }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> { AddStoryViewModel(repository) as T }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> { RegisterViewModel(repository) as T }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}