package com.wirajasa.wirajasabisnis.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseUser
import com.wirajasa.wirajasabisnis.data.model.UserProfile
import com.wirajasa.wirajasabisnis.data.repository.AuthRepository
import com.wirajasa.wirajasabisnis.data.repository.UserRepository
import com.wirajasa.wirajasabisnis.utility.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val userRepo : UserRepository
) : ViewModel() {

    fun signInWithEmailAndPassword(
        email: String, password: String
    ): LiveData<NetworkResponse<UserProfile>> {
        return auth.signInWithEmailAndPassword(email, password)
            .asLiveData(Dispatchers.Main)
    }

    fun getCurrentUser(): FirebaseUser? = auth.getCurrentUser()

    fun getProfile(): UserProfile = userRepo.getLocalProfile()
}