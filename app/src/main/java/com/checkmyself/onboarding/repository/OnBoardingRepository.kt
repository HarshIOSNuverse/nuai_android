package com.checkmyself.onboarding.repository

import com.checkmyself.network.ApiResponseState
import com.checkmyself.network.CommonResponse
import com.checkmyself.network.NetworkService
import com.checkmyself.onboarding.model.api.request.*
import com.checkmyself.onboarding.model.api.response.LoginResponse
import com.checkmyself.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnBoardingRepository @Inject constructor(private val networkService: NetworkService) {
    suspend fun login(request: LoginRequest): Flow<ApiResponseState<LoginResponse>> {
        return flow {
            val response = networkService.api.login(request)
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


//    suspend fun registerDeviceToken(request: RegisterTokenRequest) =
//        networkService.api.registerDeviceToken(request)

    suspend fun signup(request: RegisterRequest): Flow<ApiResponseState<LoginResponse>> {
        return flow {
            val response = networkService.api.signUp(request)
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

    suspend fun forgotPassword(request: ForgotPasswordRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.forgotPassword(request)
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

    suspend fun resetPassword(request: ResetPasswordRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.resetPassword(request)
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

    suspend fun resendOTP(): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.resendOTP()
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

    suspend fun resendOTPForgotPassword(request:ForgotPasswordRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.resendOTPForgotPassword(request)
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

    suspend fun verifyOTPForgotPassword(request: VerifyOTPRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.verifyOTPForgotPassword(request)
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

    suspend fun verifyOTP(request: VerifyOTPRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.verifyOTP(request)
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