package com.dicoding.picodiploma.mystorius.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mystorius.data.UserRepository
import com.dicoding.picodiploma.mystorius.data.api.RegisterResponse
import kotlinx.coroutines.launch

sealed class RegistrationState {
    object Loading : RegistrationState()
    data class Success(val response: RegisterResponse) : RegistrationState()
    data class Error(val error: String) : RegistrationState()
}

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    fun register(name: String, email: String, password: String) {
        _registrationState.value = RegistrationState.Loading
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _registrationState.value = RegistrationState.Success(response)
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Unknown error")
            }
        }
    }
}