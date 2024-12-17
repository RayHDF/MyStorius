package com.dicoding.picodiploma.mystorius.view.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.mystorius.data.StoriesRepository
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem

class StoriesViewModel(private val repository: StoriesRepository) : ViewModel() {

    fun getStoriesPagingData(token: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getStoriesWithPaging(token).cachedIn(viewModelScope)
    }
}