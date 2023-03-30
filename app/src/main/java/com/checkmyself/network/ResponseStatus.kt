package com.checkmyself.network

import java.io.Serializable

open class ResponseStatus : Serializable {

    var statusCode = 0

    var message: String? = null

    companion object {
        const val STATUS_CODE_SUCCESS = 200
        const val STATUS_CODE_CREATED = 201
//        const val STATUS_CODE_ERROR_OAUTH = 401
//        const val STATUS_CODE_ERROR_400 = 400
//        const val STATUS_CODE_ERROR_409 = 409
//        const val STATUS_CODE_ERROR_SERVER = 500
//        const val STATUS_CODE_ERROR_404 = 404
//        const val STATUS_CODE_CONFLICT = 409

        // This is custom code for handling timeout error
//        const val STATUS_CODE_ERROR_TIMEOUT = 5002
    }
}