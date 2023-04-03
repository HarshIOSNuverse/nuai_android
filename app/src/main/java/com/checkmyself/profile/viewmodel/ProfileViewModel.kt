package com.checkmyself.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.checkmyself.R
import com.checkmyself.di.ResourcesProvider
import com.checkmyself.network.ApiResponseState
import com.checkmyself.network.CommonResponse
import com.checkmyself.network.Status
import com.checkmyself.onboarding.model.api.response.LoginResponse
import com.checkmyself.onboarding.model.api.response.MyProfileResponse
import com.checkmyself.profile.model.api.request.PurchaseRequest
import com.checkmyself.profile.model.api.request.SendFeedbackRequest
import com.checkmyself.profile.model.api.request.UpdateProfileRequest
import com.checkmyself.profile.model.api.response.MyPlansResponse
import com.checkmyself.profile.model.api.response.PaymentDetailResponse
import com.checkmyself.profile.model.api.response.PaymentListResponse
import com.checkmyself.profile.model.api.response.PurchaseResponse
import com.checkmyself.profile.repository.ProfileRepository
import com.checkmyself.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    val updateProfileState = MutableStateFlow(ApiResponseState(Status.LOADING, LoginResponse()))
    val meApiState =
        MutableStateFlow(ApiResponseState(Status.LOADING, MyProfileResponse()))
    val deleteAccountState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val sendFeedbackState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val getMyPlanState = MutableStateFlow(ApiResponseState(Status.LOADING, MyPlansResponse()))
    val paymentListState = MutableStateFlow(ApiResponseState(Status.LOADING, PaymentListResponse()))
    val paymentDetailState =
        MutableStateFlow(ApiResponseState(Status.LOADING, PaymentDetailResponse()))
    val subscriptionState =
        MutableStateFlow(ApiResponseState(Status.LOADING, PurchaseResponse()))

    fun getMe() {
        if (CommonUtils.isNetworkAvailable(resourcesProvider.context)) {
            meApiState.value = ApiResponseState.loading()
            viewModelScope.launch {
                profileRepository.getMe().catch {
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
                    profileRepository.updateProfile(request).catch {
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
                profileRepository.deleteAccount().catch {
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
                profileRepository.sendFeedback(request).catch {
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

    fun getMyPlans() {
        if (CommonUtils.isNetworkAvailable(resourcesProvider.context)) {
            getMyPlanState.value = ApiResponseState.loading()
            viewModelScope.launch {
                profileRepository.getMyPlans().catch {
                    getMyPlanState.value =
                        ApiResponseState.error(it.message, 100)
                }.collect {
                    getMyPlanState.value =
                        if (it.data != null) ApiResponseState.success(it.data, it.code)
                        else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            getMyPlanState.value =
                ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
        }
    }

    fun getPaymentList(year: String, offset: Int) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                paymentListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                paymentListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    profileRepository.getPaymentList(year, offset).catch {
                        paymentListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        paymentListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getPaymentDetail(id: String?) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                paymentDetailState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                paymentDetailState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    profileRepository.getPaymentDetail(id).catch {
                        paymentDetailState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        paymentDetailState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun addSubscription(request: PurchaseRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                subscriptionState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                subscriptionState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    profileRepository.addSubscription(request).catch {
                        subscriptionState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        subscriptionState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }
}