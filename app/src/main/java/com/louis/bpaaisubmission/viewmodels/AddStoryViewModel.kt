package com.louis.bpaaisubmission.viewmodels

import androidx.lifecycle.*
import com.louis.bpaaisubmission.data.Repository
import com.louis.bpaaisubmission.data.remote.response.UploadResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.louis.bpaaisubmission.utils.Result

class AddStoryViewModel(private val repository: Repository) : ViewModel() {

    private val _result = MutableLiveData<Result<UploadResponse>>()
    val result: LiveData<Result<UploadResponse>> = _result

    fun getSession() = repository.UserPrefRepository().getSession().asLiveData()

    fun uploadNewStory(token: String, description: RequestBody, lat: RequestBody?, lon:RequestBody?, photo: MultipartBody.Part) {
        viewModelScope.launch {
            _result.value = Result.Loading
            repository.StoryRepository().uploadNewStory(token, description, lat, lon, photo).collect {
                _result.value = it
            }
        }
    }
}