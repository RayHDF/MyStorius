package com.dicoding.picodiploma.mystorius.view.stories.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mystorius.data.StoriesRepository
import com.dicoding.picodiploma.mystorius.data.api.FileUploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoriesRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    private val _uploadResult = MutableLiveData<FileUploadResponse?>()
    val uploadResult: LiveData<FileUploadResponse?> get() = _uploadResult

    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun setDescription(desc: String) {
        _description.value = desc
    }

    fun uploadImage(token: String, imageMultipart: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            val response = repository.uploadImage(token, imageMultipart, description)
            _uploadResult.value = response
        }
    }
}