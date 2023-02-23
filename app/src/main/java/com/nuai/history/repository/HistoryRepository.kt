package com.nuai.history.repository

import com.nuai.history.model.api.response.CalenderDateResponse
import com.nuai.history.model.api.response.HistoryListResponse
import com.nuai.home.model.api.request.SendScanRequest
import com.nuai.network.ApiResponseState
import com.nuai.network.CommonResponse
import com.nuai.network.NetworkService
import com.nuai.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val networkService: NetworkService) {

    suspend fun getCalenderDateByMonth(month: String): Flow<ApiResponseState<CalenderDateResponse>> {
        return flow {
            val response = networkService.api.getCalenderDateByMonth(month)
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
    suspend fun getHistoryList(date: String): Flow<ApiResponseState<HistoryListResponse>> {
        return flow {
            val response = networkService.api.getHistoryList(date)
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
    fun sendScanResult(request: SendScanRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.sendScanResult(request)
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