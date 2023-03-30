package com.checkmyself.network

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response

/**
 * Copied from Architecture components google sample:
 * https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/api/ApiResponse.kt
 */
@Suppress("unused") // T is used in extending classes
sealed class GenericApiResponse<T> {

    companion object {

        private const val TAG: String = "AppDebug"

        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error", 0)
        }

        fun <T> create(response: Response<T>): GenericApiResponse<T> {
            Log.d(TAG, "GenericApiResponse: response: $response")
            Log.d(TAG, "GenericApiResponse: raw: ${response.raw()}")
            Log.d(TAG, "GenericApiResponse: headers: ${response.headers()}")
            Log.d(TAG, "GenericApiResponse: message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                return if (body == null || response.code() == 204) {
                    ApiEmptyResponse(response.code())
                } else if (response.code() == 401) {
                    ApiErrorResponse("401 Unauthorized. Token may be invalid.", response.code())
                } else {
                    ApiSuccessResponse(body = body, response.code())
                }
            } else {
//                val msg = response.errorBody()?.string()
//                val errorMsg = if (msg.isNullOrEmpty()) {
//                    response.message()
//                } else {
//                    msg
//                }
//                return ApiErrorResponse(errorMsg ?: "unknown error")

                val gson = Gson()
                var baseResponse = BaseResponse()
                baseResponse.message = "unknown error"
                try {
                    baseResponse = gson.fromJson(response.errorBody()?.charStream(), BaseResponse::class.java)
                    baseResponse.message = baseResponse.message
                } catch (e: Exception) {
                    Log.e(TAG, "" + e.localizedMessage)
                }
                return ApiErrorResponse(baseResponse.message ?: "unknown error", response.code())
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T>(val responseCode: Int) : GenericApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T, val responseCode: Int) : GenericApiResponse<T>()

data class ApiErrorResponse<T>(val errorMessage: String, val responseCode: Int) : GenericApiResponse<T>()