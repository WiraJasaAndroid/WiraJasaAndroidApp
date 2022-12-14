package com.wirajasa.wirajasabisnis.feature_buyer.domain.repository_impl

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.wirajasa.wirajasabisnis.R
import com.wirajasa.wirajasabisnis.core.domain.model.ServicePost
import com.wirajasa.wirajasabisnis.core.domain.model.ServicePost.Companion.PROVINCE
import com.wirajasa.wirajasabisnis.core.usecases.HandleException
import com.wirajasa.wirajasabisnis.core.utility.NetworkResponse
import com.wirajasa.wirajasabisnis.feature_buyer.domain.repository.BuyerRepository
import com.wirajasa.wirajasabisnis.core.utility.constant.FirebaseCollection.SERVICE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BuyerRepositoryImpl @Inject constructor(
    private val db : FirebaseFirestore,
    private val mContext: Context,
    private val ioDispatcher: CoroutineDispatcher
) : BuyerRepository {

    override fun getAllService(): Flow<NetworkResponse<List<ServicePost>>> = flow {
        try {
            val data = db.collection(SERVICE).get().await().toObjects(ServicePost::class.java)
            emit(NetworkResponse.Success(data))
        }catch (e: Exception) {
            emit(NetworkResponse.GenericException(HandleException(mContext, e).invoke()))
        }
    }.onStart { emit(NetworkResponse.Loading(mContext.getString(R.string.loading_status_fetching_data))) }.flowOn(ioDispatcher)

    override fun getAllServiceByProvince(provinceName: String): Flow<NetworkResponse<List<ServicePost>>> = flow {
        try {
            if(provinceName.isNotEmpty()) {
                val data = db.collection(SERVICE)
                    .whereIn(PROVINCE, listOf(provinceName))
                    .get().await().toObjects(ServicePost::class.java)
                emit(NetworkResponse.Success(data))
            } else {
                val data = db.collection(SERVICE).get().await().toObjects(ServicePost::class.java)
                emit(NetworkResponse.Success(data))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.GenericException(HandleException(mContext, e).invoke()))
        }
    }.onStart { emit(NetworkResponse.Loading(mContext.getString(R.string.loading_status_fetching_data))) }.flowOn(ioDispatcher)
}