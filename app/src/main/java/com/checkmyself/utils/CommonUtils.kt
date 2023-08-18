package com.checkmyself.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.billingclient.api.ProductDetails
import com.checkmyself.R
import com.checkmyself.app.MyApplication
import com.checkmyself.network.Error
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

object CommonUtils {
    private const val TAG: String = "CommonUtils"

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun printHashKey(activity: Activity) {
        try {
            @Suppress("DEPRECATION")
            val info = activity.packageManager.getPackageInfo(
                activity.packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Logger.e(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            Logger.e(TAG, "printHashKey() $e")
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.e(TAG, "printHashKey()$e")
        }
    }

    fun getPath(context: Context, uri: Uri): String {
        var result: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        if (result == null) {
            result = ""
        }
        return result
    }

    fun getAppDirFile(direName: String): File? {
        val storageState = Environment.getExternalStorageState()
        var mediaDir: File? = MyApplication.instance?.filesDir
        try {
            mediaDir = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    MyApplication.instance
                        ?.getExternalFilesDir(Environment.DIRECTORY_PICTURES) // (1)
                }
                (storageState == Environment.MEDIA_MOUNTED) -> {
                    File(Environment.getExternalStorageDirectory().toString(), direName)
                }
                else -> {
                    File(MyApplication.instance?.filesDir, direName)
                }
            }
            if (mediaDir != null && !mediaDir.exists()) {
                mediaDir.mkdirs()
            }
        } catch (e: IOException) {
            Logger.d(TAG, "Could not create file: $e")
        }
        return mediaDir
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showToast(activity: Activity?, message: String?) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailMatcher =
            Pattern.compile("^[_A-Za-z\\d-+]+(\\.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(\\.[A-Za-z\\d]+)*(\\.[A-Za-z]{2,})\$")
                .matcher(email)
        return !TextUtils.isEmpty(email) && emailMatcher.find()
    }

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val specialCharacters = "@%!#\$^&"
        val passwordExp =
//            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
            "^(?=.*[A-Z])(?=.*[$specialCharacters])(?=[a-zA-Z\\d$specialCharacters]+$).{8,20}$"
        pattern = Pattern.compile(passwordExp)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

//    fun isAlphaNumeric(password: String): Boolean {
//        val emailExpression = "[A-Za-z0-9]+"
//        val p = Pattern.compile(emailExpression)
//        val m = p.matcher(password)
//        return !TextUtils.isEmpty(password) && m.find()
//    }

    fun <T> getStringToModel(json: String?, clazz: Class<T>?): Any {
        return Gson().fromJson(json, clazz as Type?)
    }

    fun updatePasswordViewIcon(imageView: ImageView, show: Boolean) {
        imageView.setImageResource(if (show) R.drawable.show_password else R.drawable.hide_password)
    }

    fun updatePasswordView(editText: EditText, show: Boolean) {
        editText.transformationMethod =
            if (show) null else PasswordTransformationMethod.getInstance()
        editText.setSelection(editText.text.toString().length)
    }

    /**
     * Null safe comparison of two objects.
     *
     * @return true if the objects are identical.
     */
    fun objectEquals(o1: Any?, o2: Any?): Boolean {
        if (o1 == null && o2 == null) {
            return true
        }
        return if (o1 == null) {
            false
        } else o1 == o2
    }

    fun isTimeOutError(t: Throwable?): Boolean {
        return t is SocketTimeoutException
    }

    fun getErrorResponse(error: ResponseBody?): Error {
        val gson = Gson()
        var baseResponse = Error()
        baseResponse.message = ""
        try {
            baseResponse = gson.fromJson(error!!.charStream(), Error::class.java)
            baseResponse.message = baseResponse.message
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return baseResponse
    }

    fun roundDouble(d: Double, point: Int): String {
        var tempValue = d
        val factor = 10.0.pow(point.toDouble()).toLong()
        tempValue *= factor
        val temp = tempValue.roundToLong().toDouble() / factor
        val longValue = temp.toLong()
        val difference = temp - longValue
        var value = longValue.toString()
        if (abs(difference) > 0) {
            value = temp.toString()
        }
        return value

    }

    fun roundDouble1(price: Double, numberOfFraction: Int): String {
        return String.format(Locale.US, "%,.${numberOfFraction}f", price)
    }

    fun roundDouble2(price: Double, numberOfFraction: Int): String {
        return String.format(Locale.US, "%.${numberOfFraction}f", price)
    }

    fun getFirstLatterCap(message: String?): String {
        val newMessage = java.lang.StringBuilder("")
        if (!message.isNullOrEmpty()) {
            val split = message.split(" ")
            if (split.isNotEmpty()) {
                for (word in split) {
                    newMessage.append(word.substring(0, 1).uppercase())
                    if (word.length > 1) {
                        newMessage.append(word.substring(1, word.length).lowercase())
                    }
                    newMessage.append(" ")
                }
            }
        }
        return newMessage.toString()
    }

    fun setBgColor(mContext: Context, background: Drawable, color: Int) {
        when (background) {
            is ShapeDrawable -> {
                // cast to 'ShapeDrawable'
                val shapeDrawable: ShapeDrawable = background
                shapeDrawable.paint.color =
                    if (color > 0) ContextCompat.getColor(mContext, color) else color
            }
            is GradientDrawable -> {
                // cast to 'GradientDrawable'
                val gradientDrawable: GradientDrawable = background
                gradientDrawable.setColor(
                    if (color > 0) ContextCompat.getColor(
                        mContext,
                        color
                    ) else color
                )
            }
            is ColorDrawable -> {
                // alpha value may need to be set again after this call
                val colorDrawable: ColorDrawable = background
                colorDrawable.color =
                    if (color > 0) ContextCompat.getColor(mContext, color) else color
            }
        }
    }

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun scrollToView(view: View) {
        view.parent.requestChildFocus(view, view)
    }

    fun getWellnessLevel(context: Context, wellnessScore: Int): String {
        return when (wellnessScore) {
            8, 9, 10 -> {
                context.getString(R.string.high)
            }
            4, 5, 6, 7 -> {
                context.getString(R.string.medium)
            }
            else -> {
                context.getString(R.string.low)
            }
        }
    }

    fun getStressResponseAndRecoveryAbility(value: Int): String {
        return when (value) {
            1 -> "Low"
            2 -> "Normal"
            3 -> "High"
            else -> ""
        }
    }

    fun getStressLevel(value: String?): String {
        return when (value) {
            "1" -> MyApplication.instance!!.getString(R.string.low)
            "2" -> MyApplication.instance!!.getString(R.string.normal)
            "3" -> MyApplication.instance!!.getString(R.string.mild)
            "4", "5" -> MyApplication.instance!!.getString(R.string.high)
            else -> MyApplication.instance!!.getString(R.string.na)
        }
    }

    private var currencyLocaleMap: SortedMap<Currency, Locale>? = null
    fun getCurrencySymbol(currencyCode: String?): String {
        if (currencyLocaleMap == null) {
            currencyLocaleMap = TreeMap { o1, o2 -> o1.currencyCode.compareTo(o2.currencyCode) }
            for (locale in Locale.getAvailableLocales()) {
                try {
                    val currency = Currency.getInstance(locale)
                    currencyLocaleMap!![currency] = locale
                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
                }
            }
        }
        val currency = Currency.getInstance(currencyCode)
        return currency.getSymbol(currencyLocaleMap!![currency])
    }

    fun getPriceWithCurrency(subscription: ProductDetails): String {
        return subscription.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
            0
        )?.formattedPrice!!/*getCurrencySymbol(
            subscription.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                0
            )?.priceCurrencyCode
        ) + " " + roundDouble(
            (subscription.subscriptionOfferDetails!![0]!!.pricingPhases.pricingPhaseList[0]!!.priceAmountMicros / 1000000).toDouble(),
            2
        )*/
    }

    fun getPriceWithoutCurrency(currencyCode: String, price: String): Double {
        val formatter: NumberFormat = NumberFormat.getNumberInstance(/*Locale("en","nl")*/)
        val symbol = getCurrencySymbol(currencyCode)
        return formatter.parse(price.replace(symbol, ""))!!.toString().replace(",", ".").toDouble()
    }
}