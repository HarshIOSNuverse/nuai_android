package com.checkmyself.history.repository

import com.checkmyself.history.model.api.response.CalenderDateResponse
import com.checkmyself.history.model.api.response.HistoryListResponse
import com.checkmyself.history.model.api.response.SendScanResponse
import com.checkmyself.home.model.api.request.SendScanRequest
import com.checkmyself.home.model.api.response.MeasurementResponse
import com.checkmyself.network.ApiResponseState
import com.checkmyself.network.CommonResponse
import com.checkmyself.network.NetworkService
import com.checkmyself.utils.CommonUtils
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
    fun sendScanResult(request: SendScanRequest): Flow<ApiResponseState<SendScanResponse>> {
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

    suspend fun getScanInfo(id: Long): Flow<ApiResponseState<MeasurementResponse>> {
        return flow {
            val response = networkService.api.getScanInfo(id)
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
    suspend fun deleteMeasurement(id: Long): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.deleteMeasurement(id)
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