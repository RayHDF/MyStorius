package com.dicoding.picodiploma.mystorius.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.mystorius.data.api.ApiConfig
import com.dicoding.picodiploma.mystorius.data.api.ApiService
import com.dicoding.picodiploma.mystorius.data.api.FileUploadResponse
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem
import com.dicoding.picodiploma.mystorius.data.api.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoriesRepository(private val apiService: ApiService) {
    suspend fun getStories(token: String): StoryResponse? {
        return withContext(Dispatchers.IO) {
            try {
                ApiConfig.apiService.getStories("Bearer $token")
            } catch (e: Exception) {
                Log.e("StoriesRepository", "Error getting stories", e)
                null
            }
        }
    }


    fun getStoriesWithPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, "Bearer $token")
            }
        ).liveData.also {
            it.observeForever { pagingData ->
                Log.d("StoriesRepositoryToken", "Bearer $token")
                Log.d("StoriesRepository", "Paging data loaded: $pagingData")
            }
        }
    }

    suspend fun uploadImage(token: String, imageMultiPart: MultipartBody.Part, description: RequestBody): FileUploadResponse? {
        return withContext(Dispatchers.IO) {
            try {
                ApiConfig.apiService.uploadImage("Bearer $token", imageMultiPart, description)
            } catch (e: Exception) {
                Log.e("StoriesRepository", "Error uploading image", e)
                null
            }
        }
    }

    suspend fun getStoriesWithLocation(token: String):StoryResponse? {
        return withContext(Dispatchers.IO) {
            try {
                ApiConfig.apiService.getStoriesWithLocation("Bearer $token")
            } catch (e: Exception) {
                Log.e("StoriesRepository", "Error getting stories with location: $e")
                null
            }
        }
    }
}