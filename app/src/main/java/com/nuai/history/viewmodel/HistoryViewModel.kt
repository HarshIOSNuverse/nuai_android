package com.nuai.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuai.R
import com.nuai.di.ResourcesProvider
import com.nuai.history.model.api.response.CalenderDateResponse
import com.nuai.history.model.api.response.HistoryListResponse
import com.nuai.history.model.api.response.SendScanResponse
import com.nuai.history.repository.HistoryRepository
import com.nuai.home.model.api.request.SendScanRequest
import com.nuai.home.model.api.response.MeasurementResponse
import com.nuai.network.ApiResponseState
import com.nuai.network.CommonResponse
import com.nuai.network.Status
import com.nuai.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    val calendarDateByMonthState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CalenderDateResponse()))
    val historyListState = MutableStateFlow(ApiResponseState(Status.LOADING, HistoryListResponse()))
    val sendScanResultState = MutableStateFlow(ApiResponseState(Status.LOADING, SendScanResponse()))

    val getScanInfoState = MutableStateFlow(ApiResponseState(Status.LOADING, MeasurementResponse()))
    val deleteMeasurementState = MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    fun getCalenderDateByMonth(month: String) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                calendarDateByMonthState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                calendarDateByMonthState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    historyRepository.getCalenderDateByMonth(month).catch {
                        calendarDateByMonthState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        calendarDateByMonthState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getHistoryList(date: String) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                historyListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                historyListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    historyRepository.getHistoryList(date).catch {
                        historyListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        historyListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun sendScanResult(request: SendScanRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                sendScanResultState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                sendScanResultState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    historyRepository.sendScanResult(request).catch {
                        sendScanResultState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        sendScanResultState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getScanInfo(id: Long) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                getScanInfoState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                getScanInfoState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    historyRepository.getScanInfo(id).catch {
                        getScanInfoState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        getScanInfoState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun deleteMeasurement(id: Long) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                deleteMeasurementState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                deleteMeasurementState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    historyRepository.deleteMeasurement(id).catch {
                        deleteMeasurementState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        deleteMeasurementState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }
}