package com.dicoding.picodiploma.mystorius.view.stories.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddStoryViewModel : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun setDescription(desc: String) {
        _description.value = desc
    }
}