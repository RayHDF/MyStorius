package com.dicoding.picodiploma.mystorius.data

import com.dicoding.picodiploma.mystorius.data.api.ApiConfig
import com.dicoding.picodiploma.mystorius.data.api.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoriesRepository {
    suspend fun getStories(token: String): StoryResponse? {
        return withContext(Dispatchers.IO) {
            try {
                ApiConfig.apiService.getStories("Bearer $token")
            } catch (e: Exception) {
                null
            }
        }
    }
}