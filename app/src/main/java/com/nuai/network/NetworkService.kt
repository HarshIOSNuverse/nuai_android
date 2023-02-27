package com.nuai.network

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.nuai.history.model.api.response.CalenderDateResponse
import com.nuai.history.model.api.response.HistoryListResponse
import com.nuai.history.model.api.response.SendScanResponse
import com.nuai.home.model.api.request.SendScanRequest
import com.nuai.home.model.api.response.MeasurementResponse
import com.nuai.onboarding.model.api.request.*
import com.nuai.onboarding.model.api.response.LoginResponse
import com.nuai.onboarding.model.api.response.MyProfileResponse
import com.nuai.profile.model.api.request.SendFeedbackRequest
import com.nuai.profile.model.api.request.UpdateProfileRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class NetworkService @JvmOverloads
constructor(
    context: Context,
    baseUrl: String = domainName,
    okHttpClientFactory: OkHttpClientFactory
) {

    lateinit var api: NetworkAPI

    private var mContext: Context? = null

    @JvmOverloads
    fun createNetworkService(
        context: Context?, baseUrl: String = domainName,
        okHttpClientFactory: OkHttpClientFactory
    ) {
        mContext = context
        val okHttpClient = okHttpClientFactory.getOkHttpClient()
        val retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(okHttpClient)
            .build()
        api = retrofit.create(NetworkAPI::class.java)
    }

    /**
     * all the Service all s to use for the retrofit requests.
     */
    interface NetworkAPI {
//        @Headers("$HEADER_BEAR: true")
//        @POST(Url.API + "register-device")
//        suspend fun registerDeviceToken(@Body request: RegisterTokenRequest): Response<CommonResponse?>
//
//        @Headers("$HEADER_BEAR: true")
//        @POST(Url.API + "unregister-device")
//        suspend fun unregisterToken(
//            @Query("push_type") pushType: String, @Query("token") token: String?
//        ): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "login")
        suspend fun login(@Body request: LoginRequest?): Response<LoginResponse?>

        @Headers("$HEADER_BEAR: true")
        @GET(Url.API + "me")
        suspend fun getMe(): Response<MyProfileResponse?>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "signup")
        suspend fun signUp(@Body request: RegisterRequest?): Response<LoginResponse?>

        @Headers("$HEADER_BEAR: true")
        @PUT(Url.API + "profile")
        suspend fun updateProfile(@Body request: UpdateProfileRequest?): Response<LoginResponse?>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "verify-otp")
        suspend fun verifyOTPForgotPassword(@Body request: VerifyOTPRequest?): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "resend-otp")
        suspend fun resendOTPForgotPassword(@Body request: ForgotPasswordRequest): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "otp/verify")
        suspend fun verifyOTP(@Body request: VerifyOTPRequest?): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "otp/resend")
        suspend fun resendOTP(): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: false")
        @POST(Url.API + "forgot-password")
        suspend fun forgotPassword(@Body request: ForgotPasswordRequest?): Response<CommonResponse?>

        @Headers("$HEADER_BEAR:false")
        @POST(Url.API + "reset/password")
        suspend fun resetPassword(@Body request: ResetPasswordRequest?): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: true")
        @DELETE(Url.API + "account")
        suspend fun deleteAccount(): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "feedback")
        suspend fun sendFeedback(@Body request: SendFeedbackRequest): Response<CommonResponse?>

        @Headers("$HEADER_BEAR: true")
        @GET(Url.API + "calendar")
        suspend fun getCalenderDateByMonth(@Query("period") month: String?): Response<CalenderDateResponse?>

        @Headers("$HEADER_BEAR: true")
        @GET(Url.API + "scan/history")
        suspend fun getHistoryList(@Query("date") date: String?): Response<HistoryListResponse?>

        @Headers("$HEADER_BEAR: true")
        @POST(Url.API + "scan")
        suspend fun sendScanResult(@Body request: SendScanRequest): Response<SendScanResponse?>

        @Headers("$HEADER_BEAR: true")
        @GET(Url.API + "scan/{id}")
        suspend fun getScanInfo(@Path("id") id: Long): Response<MeasurementResponse?>

        @Headers("$HEADER_BEAR: true")
        @DELETE(Url.API + "scan/{id}")
        suspend fun deleteMeasurement(@Path("id") id: Long): Response<CommonResponse?>

        /* @Headers("$HEADER_BEAR: true")
         @POST(Url.API + "register-device")
         suspend fun registerDeviceToken(@Body request: RegisterTokenRequest): Response<CommonResponse?>

         @Headers("$HEADER_BEAR: true")
         @POST(Url.API + "unregister-device")
         suspend fun unregisterToken(
             @Query("push_type") pushType: String, @Query("token") token: String?
         ): Response<CommonResponse?>*/
    }

    companion object {
        const val HEADER_USER_AGENT = "User-Agent"

        /**
         * For Header Request Key
         */
        const val HEADER_BEAR = "HeaderBear"
        const val HEADER_BASIC = "HeaderBasic"
        const val HEADER_OPTION = "HeaderOption"
        const val HEADER_APP_VERSION = "app_version"
        const val HEADER_UUID = "x-uuid"
        const val HEADER_APP_VERSION_NAME = "X-App-Version-Name"
        const val HEADER_API_VERSION = "X-Api-Version-Code"
        const val HEADER_AUTHORIZATION = "Authorization"

        private val domainName: String
            get() = Url.HOST
    }

    init {
        createNetworkService(context, baseUrl, okHttpClientFactory)
    }
}