package com.checkmyself.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.*

class ContextWrapper(base: Context?) : android.content.ContextWrapper(base) {
    companion object {
        @SuppressLint("AppBundleLocaleChanges")
        @Suppress("DEPRECATION")
        fun wrap(context1: Context, newLocale: Locale?): ContextWrapper {
            var context = context1
            val res = context.resources
            val configuration = res.configuration
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    configuration.setLocale(newLocale)
                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)
                    context = context.createConfigurationContext(configuration)
                }
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
//                    configuration.setLocale(newLocale)
//                    context = context.createConfigurationContext(configuration)
//                }
                else -> {
                    configuration.locale = newLocale
                    res.updateConfiguration(configuration, res.displayMetrics)
                }
            }
            return ContextWrapper(context)
        }
    }
}