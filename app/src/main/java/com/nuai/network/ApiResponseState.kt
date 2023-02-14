package com.nuai.network

data class ApiResponseState<out T>(
    val status: Status,
    val data: T?,
    val message: String? = null,
    val code: Int = 0
) {

    companion object {

        // In case of Success,set status as
        // Success and data as the response
        fun <T> success(data: T?, code: Int): ApiResponseState<T> {
            return ApiResponseState(Status.SUCCESS, data, null, code)
        }

        // In case of failure ,set state to Error ,
        // add the error message,set data to null
        fun <T> error(msg: String?, code: Int): ApiResponseState<T> {
            return ApiResponseState(Status.ERROR, null, msg, code)
        }

        // When the call is loading set the state
        // as Loading and rest as null
        fun <T> loading(): ApiResponseState<T> {
            return ApiResponseState(Status.LOADING, null, null, 0)
        }
    }
}

// An enum to store the
// current state of api call
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}