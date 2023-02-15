package com.nuai.profile.repository

import com.nuai.network.ApiResponseState
import com.nuai.network.NetworkService
import com.nuai.onboarding.model.api.response.LoginResponse
import com.nuai.onboarding.model.api.response.MyProfileResponse
import com.nuai.profile.model.api.request.UpdateProfileRequest
import com.nuai.utils.CommonUtils
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
}