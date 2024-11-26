package com.dicoding.picodiploma.mystorius.di

import android.content.Context
import com.dicoding.picodiploma.mystorius.data.UserRepository
import com.dicoding.picodiploma.mystorius.data.api.ApiConfig
import com.dicoding.picodiploma.mystorius.data.pref.UserPreference
import com.dicoding.picodiploma.mystorius.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.apiService
        return UserRepository.getInstance(pref, apiService)
    }
}