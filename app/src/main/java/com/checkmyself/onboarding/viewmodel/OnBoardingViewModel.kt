package com.checkmyself.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.checkmyself.R
import com.checkmyself.di.ResourcesProvider
import com.checkmyself.network.ApiResponseState
import com.checkmyself.network.CommonResponse
import com.checkmyself.network.Status
import com.checkmyself.onboarding.model.api.request.*
import com.checkmyself.onboarding.model.api.response.LoginResponse
import com.checkmyself.onboarding.repository.OnBoardingRepository
import com.checkmyself.onboarding.ui.activity.OTPVerificationActivity
import com.checkmyself.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val onBoardingRepository: OnBoardingRepository
) : ViewModel() {
    var from: Int = 0 //0-SignUp, 1-ForgotPassword
    val loginState = MutableStateFlow(
        ApiResponseState(Status.LOADING, LoginResponse())
    )
    val signupState = MutableStateFlow(ApiResponseState(Status.LOADING, LoginResponse()))
    val verifyActivationCodeState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val resendActivationCodeState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val forgotPasswordState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val resetPasswordState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))


    fun performLogin(request: LoginRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                loginState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                loginState.value = ApiResponseState.loading()

                viewModelScope.launch {
                    onBoardingRepository.login(request).catch {
                        loginState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        loginState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

//    fun registerDeviceToken() {
//        val token = Pref.fcmToken
//        if (!token.isNullOrEmpty()) {
//            val request = RegisterTokenRequest(token)
//            viewModelScope.launch(Dispatchers.IO) {
//                val response = onBoardingRepository.registerDeviceToken(request)
//                withContext(Dispatchers.Main) {
//                    var commonResponse: CommonResponse? = if (response.isSuccessful) {
//                        response.body()
//                    } else {
//                        CommonResponse().apply {
//                            error = CommonUtils.getErrorResponse(response.errorBody())
//                        }
//                    }
//                    if (commonResponse == null)
//                        commonResponse = CommonResponse()
//                    commonResponse.statusCode = response.code()
//                    registerTokenLiveData.value = commonResponse
//                }
//            }
//        }
//    }

    fun forgotPassword(email: String) {
        when {
            !CommonUtils.isNetworkAvailable(resourcesProvider.context) -> {
                forgotPasswordState.value =
                    ApiResponseState.error(
                        resourcesProvider.getString(R.string.no_internet_connection),
                        100
                    )
            }
            else -> {
                val request = ForgotPasswordRequest(email)
                forgotPasswordState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.forgotPassword(request).catch {
                        forgotPasswordState.value =
                            ApiResponseState.error(it.message, 100)
                    }.collect {
                        forgotPasswordState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun resetPassword(request: ResetPasswordRequest) {
        when {
            !CommonUtils.isNetworkAvailable(resourcesProvider.context) -> {
                resetPasswordState.value =
                    ApiResponseState.error(
                        resourcesProvider.getString(R.string.no_internet_connection),
                        100
                    )
            }
            else -> {
                resetPasswordState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.resetPassword(request).catch {
                        resetPasswordState.value =
                            ApiResponseState.error(it.message, 100)
                    }.collect {
                        resetPasswordState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun performSignup(request: RegisterRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                signupState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                signupState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.signup(request).catch {
                        signupState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        signupState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun resendOTP(email: String?) {
        if (CommonUtils.isNetworkAvailable(resourcesProvider.context)) {
            resendActivationCodeState.value = ApiResponseState.loading()
            viewModelScope.launch {
                if (from == OTPVerificationActivity.Companion.From.FORGOT_PASSWORD) {
                    val request = ForgotPasswordRequest(email)
                    onBoardingRepository.resendOTPForgotPassword(request).catch {
                        resendActivationCodeState.value =
                            ApiResponseState.error(it.message, 100)
                    }.collect {
                        resendActivationCodeState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                } else {
                    onBoardingRepository.resendOTP().catch {
                        resendActivationCodeState.value =
                            ApiResponseState.error(it.message, 100)
                    }.collect {
                        resendActivationCodeState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        } else {
            resendActivationCodeState.value =
                ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
        }
    }

    fun verifyOTP(email: String?, otp: String) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                verifyActivationCodeState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }
            else -> {
                val request = VerifyOTPRequest(email, otp)
                verifyActivationCodeState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    if (from == OTPVerificationActivity.Companion.From.FORGOT_PASSWORD) {
                        onBoardingRepository.verifyOTPForgotPassword(request).catch {
                            verifyActivationCodeState.value =
                                ApiResponseState.error(it.message, 100)
                        }.collect {
                            verifyActivationCodeState.value =
                                if (it.data != null) ApiResponseState.success(it.data, it.code)
                                else ApiResponseState.error(it.message, it.code)
                        }
                    } else {
                        onBoardingRepository.verifyOTP(request).catch {
                            verifyActivationCodeState.value =
                                ApiResponseState.error(it.message, 100)
                        }.collect {
                            verifyActivationCodeState.value =
                                if (it.data != null) ApiResponseState.success(it.data, it.code)
                                else ApiResponseState.error(it.message, it.code)
                        }
                    }


                }
            }
        }
    }
}