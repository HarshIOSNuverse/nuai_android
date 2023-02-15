package com.nuai.utils

import android.content.Context
import android.content.SharedPreferences
import com.nuai.BuildConfig
import com.nuai.app.MyApplication
import com.nuai.network.NetworkUtils
import com.nuai.profile.model.User
import com.google.gson.Gson

object Pref {
    object PrefConstant {
        const val UUID = "uuid"
        const val ACCESS_TOKEN = "access_token"
        const val APP_LOCALE = "app_locale"
        const val USER_OBJECT = "user_object"
        const val FCM_TOKEN = "fcm_token"
        const val IS_SEND_FCM_TOKEN = "is_send_fcm_token"
    }

    private val prefs: SharedPreferences =
        MyApplication.instance!!.applicationContext!!.getSharedPreferences(
            BuildConfig.PREF_FILE,
            Context.MODE_PRIVATE
        )
    private val gson = Gson()

    var uuid: String?
        get() = prefs.getString(PrefConstant.UUID, "")
        set(value) {
            prefs.edit().putString(PrefConstant.UUID, value).apply()
            NetworkUtils.putAuthParam(
                NetworkUtils.X_UUID, if (!value.isNullOrEmpty()) value else ""
            )
        }
    var accessToken: String?
        get() = prefs.getString(PrefConstant.ACCESS_TOKEN, "")
        set(value) {
            prefs.edit().putString(PrefConstant.ACCESS_TOKEN, value).apply()
            NetworkUtils.putAuthParam(
                NetworkUtils.AUTHORIZATION, if (!value.isNullOrEmpty()) "Bearer $value" else ""
            )
        }

    var appLocale: String
        get() = prefs.getString(PrefConstant.APP_LOCALE, "en")!!
        set(value) = prefs.edit().putString(PrefConstant.APP_LOCALE, value).apply()


    var fcmToken: String?
        get() = prefs.getString(PrefConstant.FCM_TOKEN, "")
        set(value) = prefs.edit().putString(PrefConstant.FCM_TOKEN, value).apply()

    var isSendFcmToken: Boolean
        get() = prefs.getBoolean(PrefConstant.IS_SEND_FCM_TOKEN, false)
        set(value) = prefs.edit().putBoolean(PrefConstant.IS_SEND_FCM_TOKEN, value).apply()

    var user: User?
        get() = gson.fromJson(
            prefs.getString(PrefConstant.USER_OBJECT, null),
            User::class.java
        )
        set(value) = prefs.edit().putString(PrefConstant.USER_OBJECT, gson.toJson(value))
            .apply()

    //    var customSizes: ArrayList<Size1>?
//        get() {
//            return try {
//                val gson = Gson()
//                val json = prefs.getString(PrefConst.CUSTOM_SIZES, null)
//                val type = object : TypeToken<ArrayList<Size1>>() {}.type
//                gson.fromJson(json, type)
//            } catch (ex: Exception) {
//                Logger.logError("Prefs", ex.toString())
//                null
//            }
//        }
//        set(value) {
//            val editor = prefs.edit()
//            val gson = Gson()
//            val json = gson.toJson(value)
//            editor.putString(Constants.PrefConst.CUSTOM_SIZES, json)
//            editor.apply()
//        }
//
    fun logout() {
        user = null
        removeTokens()
    }

    private fun removeTokens() {
        prefs.edit().remove(PrefConstant.ACCESS_TOKEN).apply()
        NetworkUtils.clearAuthParams()
    }
}