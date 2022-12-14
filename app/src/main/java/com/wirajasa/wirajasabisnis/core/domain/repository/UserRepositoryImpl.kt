package com.wirajasa.wirajasabisnis.core.domain.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.wirajasa.wirajasabisnis.R
import com.wirajasa.wirajasabisnis.core.crypto_pref.CryptoPref
import com.wirajasa.wirajasabisnis.core.domain.model.UserProfile
import com.wirajasa.wirajasabisnis.core.usecases.HandleException
import com.wirajasa.wirajasabisnis.core.utility.NetworkResponse
import com.wirajasa.wirajasabisnis.core.utility.constant.FirebaseCollection.USER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class UserRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseFirestore,
    private val context: Context,
    private val ioDispatcher: CoroutineContext,
    private val cryptoPref: CryptoPref
) : UserRepository {

    override fun updateProfile(userProfile: UserProfile): Flow<NetworkResponse<Boolean>> =
        flow {
            try {
                val newImageUri = if (isUriDifferent(userProfile))
                    storage.reference.child("$USER/${userProfile.uid}.png")
                        .putFile(Uri.parse(userProfile.image))
                        .await()
                        .storage.downloadUrl.await().toString()
                else cryptoPref.getProfile().image

                emit(NetworkResponse.Loading(context.getString(R.string.loading_status_updating_profile_data)))
                userProfile.image = newImageUri
                saveProfile(userProfile)
                db.collection(USER).document(userProfile.uid)
                    .set(userProfile, SetOptions.merge())
                    .continueWith {
                        val updatedAt = hashMapOf(
                            "updated_at" to Timestamp.now()
                        )
                        db.collection(USER).document(userProfile.uid)
                            .set(updatedAt, SetOptions.merge())
                    }.await()

                emit(NetworkResponse.Success(true))
            } catch (e: Exception) {
                Log.e(TAG, "updateProfile: ${e.stackTrace}")
                emit(NetworkResponse.GenericException(HandleException(context, e).invoke()))
            }
        }.onStart { emit(NetworkResponse.Loading(context.getString(R.string.loading_status_uploading_image))) }
            .flowOn(ioDispatcher)

    private fun isUriDifferent(userProfile: UserProfile) : Boolean =
        userProfile.image != cryptoPref.getProfile().image

    override fun getLocalProfile(): UserProfile {
        return cryptoPref.getProfile()
    }

    private fun saveProfile(profile: UserProfile) {
        cryptoPref.saveProfile(profile)
    }
}