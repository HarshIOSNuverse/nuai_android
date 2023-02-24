package com.nuai.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.location.Address
import android.location.Geocoder
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
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.google.gson.Gson
import com.nuai.R
import com.nuai.app.MyApplication
import com.nuai.network.Error
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

object CommonUtils {
    val TAG: String = "CommonUtils"

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
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

    fun copyFileOrDirectory(srcDir: String, dstDir: String) {
        val src = File(srcDir)
        val dst = File(dstDir, src.name)

        try {
            if (src.isDirectory) {
                val files = src.list()
                if (!files.isNullOrEmpty()) {
                    val filesLength = files.size
                    for (i in 0 until filesLength) {
                        val src1 = File(src, files[i]).path
                        val dst1 = dst.path
                        copyFileOrDirectory(src1, dst1)
                    }
                }
            } else {
                copyDirectoryOneLocationToAnotherLocation(src, dst)
            }
        } catch (e: Exception) {
            if (src.exists()) {
                src.delete()
            }
            if (dst.exists()) {
                dst.delete()
            }
            e.printStackTrace()
        }
    }

    fun openGallery(galleyPickLauncher: ActivityResultLauncher<Intent>, type1: String) {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = type1
        }.run {
            galleyPickLauncher.launch(this)
        }
    }

    fun getRightAngleImage(photoPath: String): String {

        try {
            val ei = ExifInterface(photoPath)
            val degree = when (ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }

            return rotateImage(degree, photoPath)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return photoPath
    }

    @Throws(IOException::class)
    fun copyDirectoryOneLocationToAnotherLocation(sourceLocation: File, targetLocation: File) {

        if (sourceLocation.isDirectory) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir()
            }

            val children = sourceLocation.list()
            if (!sourceLocation.listFiles().isNullOrEmpty() && !children.isNullOrEmpty()) {
                for (i in sourceLocation.listFiles()!!.indices) {

                    copyDirectoryOneLocationToAnotherLocation(
                        File(sourceLocation, children[i]),
                        File(targetLocation, children[i])
                    )
                }
            }
        } else {

            val inputStream = FileInputStream(sourceLocation)

            val out = FileOutputStream(targetLocation)

            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                out.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }

            inputStream.close()
            out.close()
        }
    }

    private fun rotateImage(degree: Int, imagePath: String): String {

        if (degree <= 0) {
            return imagePath
        }
        try {
            var b = BitmapFactory.decodeFile(imagePath)

            val matrix = Matrix()
            if (b.width > b.height) {
                matrix.setRotate(degree.toFloat())
                b = Bitmap.createBitmap(
                    b, 0, 0, b.width, b.height,
                    matrix, true
                )
            }

            val fOut = FileOutputStream(imagePath)
            val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(imagePath)
            if (imageType.equals("png", ignoreCase = true)) {
                b.compress(Bitmap.CompressFormat.PNG, 50, out)
            } else if (imageType.equals("jpeg", ignoreCase = true)
                || imageType.equals("jpg", ignoreCase = true)
            ) {
                b.compress(Bitmap.CompressFormat.JPEG, 50, out)
            }
            fOut.flush()
            fOut.close()

            b.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager?.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo ?: return false
            return activeNetworkInfo.isConnected
        }
    }

    fun showToast(activity: Activity?, message: String?) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailMatcher =
            Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$")
                .matcher(email)
        return !TextUtils.isEmpty(email) && emailMatcher.find()
    }

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val passwordExp =
//            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
            "^(?=.*[a-zA-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
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

//    fun getRoundDoubleWithCurrency(value: Double, digit: Int): String {
//        var digits = digit
//        val amount1 = roundDouble(value, digit)
//        if (!amount1.contains(".")) {
//            digits = 0
//        }
//        val localeForCurrency = Locale("en", "lb")
//        val formatter =
//            NumberFormat.getCurrencyInstance(localeForCurrency) as DecimalFormat
//        formatter.maximumFractionDigits = digits
//        val currencySymbol =
//            Currency.getInstance(localeForCurrency).getSymbol(localeForCurrency)
//        return formatter.format(value).replace(currencySymbol, Constants.CURRENCY)
////        return formatter.format(value)
//    }

    fun getImage(context: Context, ImageName: String?): Drawable? {
        return try {
            ContextCompat.getDrawable(
                context, context.resources
                    .getIdentifier(ImageName, "drawable", context.packageName)
            )
        } catch (e: java.lang.Exception) {
            ContextCompat.getDrawable(
                context, context.resources.getIdentifier("flag_00", "drawable", context.packageName)
            )
        }
    }

    fun setBgColor(mContext: Context, background: Drawable, color: Int) {
        when (background) {
            is ShapeDrawable -> {
                // cast to 'ShapeDrawable'
                val shapeDrawable: ShapeDrawable = background
                shapeDrawable.paint.color = ContextCompat.getColor(mContext, color)
            }
            is GradientDrawable -> {
                // cast to 'GradientDrawable'
                val gradientDrawable: GradientDrawable = background
                gradientDrawable.setColor(ContextCompat.getColor(mContext, color))
            }
            is ColorDrawable -> {
                // alpha value may need to be set again after this call
                val colorDrawable: ColorDrawable = background
                colorDrawable.color = ContextCompat.getColor(mContext, color)
            }
        }
    }

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun getAddressFromLAtLng(
        context: Context, latitude: Double, longitude: Double, isShortAddress: Boolean
    ): String? {
        var returnAddress: String? = null
        val addresses: List<Address>?
        val geoCoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geoCoder.getFromLocation(
                latitude, longitude, 1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (!addresses.isNullOrEmpty()) {
                val address: String? = addresses[0]
                    .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                val locality: String? = addresses[0].locality
                val knownName: String? = addresses[0].featureName

                if (isShortAddress) {
                    if (!knownName.isNullOrEmpty()) {
                        returnAddress = knownName
                    } else if (!addresses[0].subLocality.isNullOrEmpty()) {
                        returnAddress = addresses[0].subLocality
                    } else if (!addresses[0].subAdminArea.isNullOrEmpty()) {
                        returnAddress = addresses[0].subAdminArea
                    } else if (!locality.isNullOrEmpty()) {
                        returnAddress = locality
                    } else if (!address.isNullOrEmpty()) {
                        returnAddress = address
                    }
                } else {
                    // Need to change as per requirement
                    if (!address.isNullOrEmpty()) {
                        returnAddress = address
                    } else {
                        if (!knownName.isNullOrEmpty()) {
                            returnAddress = knownName
                        }
                        if (!addresses[0].subLocality.isNullOrEmpty()) {
                            if (!returnAddress.isNullOrEmpty())
                                returnAddress += ", "
                            returnAddress += addresses[0].subLocality
                        }
                        if (!locality.isNullOrEmpty()) {
                            if (!returnAddress.isNullOrEmpty())
                                returnAddress += ", "
                            returnAddress += locality
                        }
                        if (!addresses[0].subAdminArea.isNullOrEmpty()) {
                            if (!returnAddress.isNullOrEmpty())
                                returnAddress += ", "
                            returnAddress += addresses[0].subAdminArea
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return returnAddress
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
}