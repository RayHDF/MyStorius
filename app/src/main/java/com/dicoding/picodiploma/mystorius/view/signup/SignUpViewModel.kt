package com.dicoding.picodiploma.mystorius.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mystorius.data.UserRepository
import com.dicoding.picodiploma.mystorius.data.api.RegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successResponse = MutableLiveData<RegisterResponse>()
    val successResponse: LiveData<RegisterResponse> = _successResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _isLoading.value = false
                _successResponse.value = response
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }
}