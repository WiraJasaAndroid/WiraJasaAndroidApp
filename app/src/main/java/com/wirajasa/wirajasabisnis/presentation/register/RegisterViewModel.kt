package com.wirajasa.wirajasabisnis.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wirajasa.wirajasabisnis.data.model.UserProfile
import com.wirajasa.wirajasabisnis.data.repository.AuthRepository
import com.wirajasa.wirajasabisnis.data.repository.UserRepository
import com.wirajasa.wirajasabisnis.utility.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ): LiveData<NetworkResponse<Boolean>> {
        return authRepository.signUpWithEmailAndPassword(email, password)
            .asLiveData(Dispatchers.Main)
    }

    fun registerDefaultProfile(): LiveData<NetworkResponse<UserProfile>> {
        return userRepository.registerDefaultProfile().asLiveData(Dispatchers.Main)
    }
}