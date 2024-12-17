package com.dicoding.picodiploma.mystorius.view.stories.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mystorius.data.StoriesRepository
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoriesRepository): ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            val response = repository.getStoriesWithLocation(token)
            response?.let {
                _stories.postValue(it.listStory)
            }
        }
    }
}