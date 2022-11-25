package com.wirajasa.wirajasabisnis.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.wirajasa.wirajasabisnis.presentation.service.ServicePost
import com.wirajasa.wirajasabisnis.usecases.HandleException
import com.wirajasa.wirajasabisnis.utility.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ProductRepositoryImpl @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    private val firestoreDb: FirebaseFirestore,
    private val ioDispatcher: CoroutineContext
) : ProductRepository {

    override fun addProduct(
        uid: String,
        name: String,
        price: Int,
        unit: String,
        address: String,
        Email: String,
        phoneNumber: String,
        photo: Uri?
    ): Flow<NetworkResponse<Boolean>> = flow {
        try {
            val photoReference = storage.child("services/${System.currentTimeMillis()}-photo.jpg")
            photoReference.putFile(photo!!)
                .continueWithTask {
                    photoReference.downloadUrl
                }.continueWithTask { downloadUrlTask ->
                    val servicePost = ServicePost(
                        uid,
                        name,
                        price,
                        unit,
                        address,
                        Email,
                        phoneNumber,
                        downloadUrlTask.result.toString()
                    )
                    val serviceId = auth.currentUser?.uid + name.lowercase().trim()
                    firestoreDb.collection("service").document(serviceId).set(servicePost)
                }.await()
            emit(NetworkResponse.Success(data = true))
        } catch (e: Exception) {
            emit(NetworkResponse.GenericException(HandleException(context).getMessage(e)))
        }
    }.onStart { emit(NetworkResponse.Loading(null)) }.flowOn(ioDispatcher)
}