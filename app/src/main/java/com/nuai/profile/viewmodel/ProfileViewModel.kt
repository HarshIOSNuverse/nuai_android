package com.nuai.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuai.R
import com.nuai.di.ResourcesProvider
import com.nuai.network.ApiResponseState
import com.nuai.network.CommonResponse
import com.nuai.network.Status
import com.nuai.onboarding.model.api.response.LoginResponse
import com.nuai.onboarding.model.api.response.MyProfileResponse
import com.nuai.profile.model.api.request.SendFeedbackRequest
import com.nuai.profile.model.api.request.UpdateProfileRequest
import com.nuai.profile.repository.ProfileRepository
import com.nuai.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val onBoardingRepository: ProfileRepository
) : ViewModel() {
    val updateProfileState = MutableStateFlow(ApiResponseState(Status.LOADING, LoginResponse()))
    val meApiState =
        MutableStateFlow(ApiResponseState(Status.LOADING, MyProfileResponse()))
    val deleteAccountState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val sendFeedbackState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))

    fun getMe() {
        if (CommonUtils.isNetworkAvailable(resourcesProvider.context)) {
            meApiState.value = ApiResponseState.loading()
            viewModelScope.launch {
                onBoardingRepository.getMe().catch {
                    meApiState.value =
                        ApiResponseState.error(it.message, 100)
                }.collect {
                    meApiState.value =
                        if (it.data != null) ApiResponseState.success(it.data, it.code)
                        else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            meApiState.value =
                ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                updateProfileState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                updateProfileState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.updateProfile(request).catch {
                        updateProfileState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        updateProfileState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun deleteAccount() {
        if (CommonUtils.isNetworkAvailable(resourcesProvider.context)) {
            deleteAccountState.value = ApiResponseState.loading()
            viewModelScope.launch {
                onBoardingRepository.deleteAccount().catch {
                    deleteAccountState.value =
                        ApiResponseState.error(it.message, 100)
                }.collect {
                    deleteAccountState.value =
                        if (it.data != null) ApiResponseState.success(it.data, it.code)
                        else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            deleteAccountState.value =
                ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
        }
    }

    fun sendFeedback(request: SendFeedbackRequest) {
        if (CommonUtils.isNetworkAvailable(resourcesProvider.context)) {
            sendFeedbackState.value = ApiResponseState.loading()
            viewModelScope.launch {
                onBoardingRepository.sendFeedback(request).catch {
                    sendFeedbackState.value =
                        ApiResponseState.error(it.message, 100)
                }.collect {
                    sendFeedbackState.value =
                        if (it.data != null) ApiResponseState.success(it.data, it.code)
                        else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            sendFeedbackState.value =
                ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
        }
    }

}