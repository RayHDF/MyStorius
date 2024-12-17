package com.dicoding.picodiploma.mystorius.data

import android.util.Log
import com.dicoding.picodiploma.mystorius.data.api.ApiConfig
import com.dicoding.picodiploma.mystorius.data.api.FileUploadResponse
import com.dicoding.picodiploma.mystorius.data.api.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoriesRepository {
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