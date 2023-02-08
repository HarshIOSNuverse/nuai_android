package com.nuai.utils

import android.util.Log
import com.nuai.BuildConfig

object Logger {
    private val TAG = Logger::class.java.simpleName

    fun i(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg)
        }
    }

    fun i(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg)
        }
    }

    fun d(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg)
        }
    }

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg)
        }
    }

    fun e(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg)
        }
    }

    fun errorLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message)
        }
    }
}
