package com.dicoding.picodiploma.mystorius.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.mystorius.data.StoriesRepository
import com.dicoding.picodiploma.mystorius.data.UserRepository
import com.dicoding.picodiploma.mystorius.di.Injection
import com.dicoding.picodiploma.mystorius.view.login.LoginViewModel
import com.dicoding.picodiploma.mystorius.view.main.MainViewModel
import com.dicoding.picodiploma.mystorius.view.signup.SignupViewModel
import com.dicoding.picodiploma.mystorius.view.stories.StoriesViewModel
import com.dicoding.picodiploma.mystorius.view.stories.addstory.AddStoryViewModel
import com.dicoding.picodiploma.mystorius.view.stories.maps.MapsViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storiesRepository: StoriesRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storiesRepository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storiesRepository) as T
            }

            modelClass.isAssignableFrom(StoriesViewModel::class.java) -> {
                StoriesViewModel(storiesRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideStoriesRepository()
                )
            }.also { instance = it }
    }
}