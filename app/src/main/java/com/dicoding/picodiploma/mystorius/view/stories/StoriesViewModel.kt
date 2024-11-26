package com.dicoding.picodiploma.mystorius.view.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mystorius.data.StoriesRepository
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem
import kotlinx.coroutines.launch

class StoriesViewModel : ViewModel() {
    private val repository = StoriesRepository()
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    fun fetchStories(token: String) {
        viewModelScope.launch {
            val response = repository.getStories(token)
            response?.let {
                _stories.postValue(it.listStory)
            }
        }
    }
}