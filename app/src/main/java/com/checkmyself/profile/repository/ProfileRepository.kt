package com.checkmyself.profile.repository

import com.checkmyself.network.ApiResponseState
import com.checkmyself.network.CommonResponse
import com.checkmyself.network.NetworkService
import com.checkmyself.onboarding.model.api.response.LoginResponse
import com.checkmyself.onboarding.model.api.response.MyProfileResponse
import com.checkmyself.profile.model.api.request.CheckVersionRequest
import com.checkmyself.profile.model.api.request.PurchaseRequest
import com.checkmyself.profile.model.api.request.SendFeedbackRequest
import com.checkmyself.profile.model.api.request.UpdateProfileRequest
import com.checkmyself.profile.model.api.response.CheckVersionResponse
import com.checkmyself.profile.model.api.response.MyPlansResponse
import com.checkmyself.profile.model.api.response.PaymentDetailResponse
import com.checkmyself.profile.model.api.response.PaymentListResponse
import com.checkmyself.profile.model.api.response.PurchaseResponse
import com.checkmyself.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(private val networkService: NetworkService) {
    suspend fun getMe(): Flow<ApiResponseState<MyProfileResponse>> {
        return flow {
            val response = networkService.api.getMe()
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Flow<ApiResponseState<LoginResponse>> {
        return flow {
            val response = networkService.api.updateProfile(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteAccount(): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.deleteAccount()
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendFeedback(request: SendFeedbackRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.sendFeedback(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMyPlans(): Flow<ApiResponseState<MyPlansResponse>> {
        return flow {
            val response = networkService.api.getMyPlans()
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPaymentList(
        year: String, offset: Int
    ): Flow<ApiResponseState<PaymentListResponse>> {
        return flow {
            val response = networkService.api.getPaymentList(year, offset)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPaymentDetail(id: String?): Flow<ApiResponseState<PaymentDetailResponse>> {
        return flow {
            val response = networkService.api.getPaymentDetail(id)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addSubscription(request: PurchaseRequest): Flow<ApiResponseState<PurchaseResponse>> {
        return flow {
            val response = networkService.api.addSubscription(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkVersion(request: CheckVersionRequest): Flow<ApiResponseState<CheckVersionResponse>> {
        return flow {
            val response = networkService.api.checkVersion(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

}