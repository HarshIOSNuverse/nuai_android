package com.checkmyself.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.checkmyself.R
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/*
    This is Utility file which contains all functions that has been used throughout the application
 */
object CustomFunction {
        private const val HIDE_THRESHOLD = 100f
        private const val SHOW_THRESHOLD = 50f
        private var scrollDist = 0
        private var isVisible = true
        fun isValidEmail(target: CharSequence?): Boolean {
            return if (target == null) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }

        fun isPasswordLengthValid(target: CharSequence?): Boolean {
            return if (target == null) {
                false
            } else {
                target.length > 7
            }
        }

        fun getTypeface(context: Context, fontName: String?): Typeface {
            return Typeface.createFromAsset(context.assets, fontName)
        }

        fun setLinearRecyclerView(context: Context?, rv: RecyclerView, spacing: Int) {
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rv.layoutManager = linearLayoutManager
            //        rv.setPadding(spacing, spacing, spacing, spacing);
            rv.clipToPadding = false
            rv.clipChildren = false
            rv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
                        outRect[0, 0, spacing] = 0
                    }
                }
            })
        }

        fun setLinearBottomSpaceRecyclerView(context: Context?, rv: RecyclerView, spacing: Int) {
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = linearLayoutManager
            //        rv.setPadding(spacing, spacing, spacing, spacing);
            rv.clipToPadding = false
            rv.clipChildren = false
            rv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
                        outRect[0, 0, 0] = spacing
                    }
                }
            })
        }

        fun getUserRating(feedPrice: Int, feedValue: Int, feedQuality: Int): Float {
            val priceStar = feedPrice / 20f
            val valueStar = feedValue / 20f
            val qualityStar = feedQuality / 20f
            return Math.round((priceStar + valueStar + qualityStar) / 3).toFloat()
        }

        fun getLastMsgDateTime(dateTime: String?): String {
            var timeInMilliseconds: Long = 0
            var msgDateTime = ""
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            try {
                val updatedTime = input.parse(dateTime)
                timeInMilliseconds = updatedTime.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return if (DateUtils.isToday(timeInMilliseconds)) {
                val outputDate = SimpleDateFormat("h:mm a", Locale.US)
                try {
                    val inputDate = input.parse(dateTime)
                    msgDateTime = outputDate.format(inputDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                msgDateTime
            } else {
                val outputDate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                try {
                    val inputDate = input.parse(dateTime)
                    msgDateTime = outputDate.format(inputDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                msgDateTime
            }
        }

        fun getUpdateTime(updateTime: String?): String {
            var timeInMilliseconds: Long = 0
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            try {
                val updatedTime = input.parse(updateTime)
                timeInMilliseconds = updatedTime.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - timeInMilliseconds
            val seconds = diff / 1000
            val minutes = Math.round((seconds / 60).toFloat())
            val hours = Math.round((seconds / 3600).toFloat())
            val days = Math.round((seconds / 86400).toFloat())
            val weeks = Math.round((seconds / 604800).toFloat())
            val months = Math.round((seconds / 2600640).toFloat())
            val years = Math.round((seconds / 31207680).toFloat())
            return if (seconds < 60) {
                if (seconds == 1L) "1 second ago" else "$seconds seconds ago"
            } else if (minutes <= 60) {
                if (minutes == 1) "one minute ago" else "$minutes minutes ago"
            } else if (hours <= 24) {
                if (hours == 1) "an hour ago" else "$hours hours ago"
            } else if (days <= 7) {
                if (days == 1) "one day ago" else "$days days ago"
            } else if (weeks <= 4.3) {
                if (weeks == 1) "a week ago" else "$weeks weeks ago"
            } else if (months <= 12) {
                if (months == 1) "a month ago" else "$months months ago"
            } else {
                if (years == 1) "one year ago" else "$years years ago"
            }
        }

        fun getDateTitle(updateTime: String?): Int {

            // Today = return 1
            // Last week = return 2
            // else return = 3
            var timeInMilliseconds: Long = 0
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val todayDateFormat = SimpleDateFormat("dd MMM yy", Locale.US)
            var today: String? = ""
            try {
                val updatedTime = input.parse(updateTime)
                today = todayDateFormat.format(updatedTime)
                timeInMilliseconds = updatedTime.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - timeInMilliseconds
            val seconds = diff / 1000
            val minutes = Math.round((seconds / 60).toFloat())
            val hours = Math.round((seconds / 3600).toFloat())
            val days = Math.round((seconds / 86400).toFloat())
            val weeks = Math.round((seconds / 604800).toFloat())
            val months = Math.round((seconds / 2600640).toFloat())
            val years = Math.round((seconds / 31207680).toFloat())
            try {
                val updatedTime = input.parse(updateTime)
                timeInMilliseconds = updatedTime.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            var dateTitle = ""
            var returnvalue = 0
            if (seconds < 60) {
                dateTitle = if (seconds == 1L) "1 second ago" else "$seconds seconds ago"
                returnvalue = 1
            } else if (minutes <= 60) {
                dateTitle = if (minutes == 1) "one minute ago" else "$minutes minutes ago"
                returnvalue = 1
            } else if (hours <= 24) {
                dateTitle = if (hours == 1) "an hour ago" else "$hours hours ago"
                returnvalue = 1
            } else if (days <= 7) {
                dateTitle = if (days == 1) "one day ago" else "$days days ago"
                returnvalue = 2
            } else if (weeks <= 4.3) {
                dateTitle = if (weeks == 1) "a week ago" else "$weeks weeks ago"
                returnvalue = 3
            } else if (months <= 12) {
                dateTitle = if (months == 1) "a month ago" else "$months months ago"
                returnvalue = 3
            } else {
                dateTitle = if (years == 1) "one year ago" else "$years years ago"
                returnvalue = 3
            }
            Log.e("CustomFunction", "getDateTitle: dateTitle: $dateTitle")
            return returnvalue
        }

        fun getAmount(amount: String): String {
            val df = DecimalFormat("###.##")
            //        System.out.println("Amount: " + Double.parseDouble(df.format(Double.parseDouble(amount))));
            return df.format(amount.toDouble())
        }

        fun getQuantity(amount: String): Int {
            val df = DecimalFormat("###")
            //        System.out.println("Amount: " + Double.parseDouble(df.format(Double.parseDouble(amount))));
            return df.format(amount.toDouble()).toInt()
        }

        fun getDiscountRate(costprice: String, discountAmount: String): String {
            var discountAmount = discountAmount
            discountAmount = discountAmount.replace("-", "")
            val df = DecimalFormat("###.##")
            val rate = discountAmount.toDouble() * 100 / costprice.toDouble()
            //        System.out.println("Rate: " + Double.parseDouble(df.format(rate)));
            return df.format(rate)
        }

        @SuppressLint("NewApi")
        fun isDateOLderThan30days(newDateTime: String): Boolean {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
            val newDate = LocalDate.parse(newDateTime.split(" ").toTypedArray()[0], formatter)
            val currentDate = LocalDate.now()
            val mDateLast30Days = LocalDate.now().plusDays(-30)

//        Log.d("CustomFunction", "currentDate: " + currentDate + "  mDateLast30Days: " + mDateLast30Days);
            return if (newDate.isAfter(mDateLast30Days)) {
//            Log.d("CustomFunction", "date: " + newDate + " is older than 30 Days!!");
                true
            } else {
                false
            }
        }

        @SuppressLint("NewApi")
        fun isDateOLderThan6Months(newDateTime: String): Boolean {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
            val newDate = LocalDate.parse(newDateTime.split(" ").toTypedArray()[0], formatter)
            val mDateLast30Days = LocalDate.now().plusDays(-30)
            //        LocalDate sixMonthsAgo = LocalDate.now().plusMonths(-1);  // 6 months ago
            val sixMonthsAgo = LocalDate.now().plusDays(-40)
            val remaining = LocalDate.now().plusDays(-50)

//        Log.e("CustomFunction", "  sixMonthsAgo: " + sixMonthsAgo);
            return if (newDate.isBefore(sixMonthsAgo) && newDate.isAfter(remaining)) {
//            Log.e("CustomFunction", "date: " + newDate + " is older than 6 Months!!");
                true
            } else {
                false
            }
        }

        @SuppressLint("NewApi")
        fun isDateRemaining(newDateTime: String): Boolean {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
            val newDate = LocalDate.parse(newDateTime.split(" ").toTypedArray()[0], formatter)
            val currentDate = LocalDate.now()
            //        LocalDate sixMonthsAgo = LocalDate.now().plusMonths(-1).plusDays(-15);
            val sixMonthsAgo = LocalDate.now().plusDays(-40)
            val remaining = LocalDate.now().plusDays(-50)

//        Log.e("CustomFunction", "currentDate: " + currentDate + "  sixMonthsAgo: " + sixMonthsAgo);
            return if (newDate.isBefore(remaining)) {
//            Log.e("CustomFunction", "date: " + newDate + " is older than 6 Months!!");
                true
            } else {
                false
            }
        }

        fun getDeliveryDate(newDateTime: String?): String {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var date: Date? = null
            try {
                date = dateFormat.parse(newDateTime)
                dateFormat = SimpleDateFormat("d MMM, yyyy")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            //        Log.e("MyOrdersActivity", String.format("Delivery Date: " + dateFormat.format(date)));
            return dateFormat.format(date)
        }

        fun getCompleteCancelDate(newDateTime: String?): String {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var date: Date? = null
            try {
                date = dateFormat.parse(newDateTime)
                dateFormat = SimpleDateFormat("E ,MMM dd yyyy")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            //        Log.e("MyOrdersActivity", String.format("Delivery Date: " + dateFormat.format(date)));
            return dateFormat.format(date)
        }

        fun updateTextColorRed(
            mContext: Context?,
            fullString: String,
            subString: String,
            mTextView: TextView
        ) {
            val s: Spannable = SpannableString(fullString)
            val start = fullString.indexOf(subString)
            val end = start + subString.length
            s.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(mContext!!, R.color.red)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            mTextView.setText(s, TextView.BufferType.SPANNABLE)
        }

        fun getFonts(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets, "red_rose_regular.ttf")
        }

        fun updateBoldToSubString(
            mContext: Context?,
            fullString: String,
            subString: String,
            mTextView: TextView
        ) {
            Log.e(
                "CustomFunction",
                "updateNormaltoSubString fullString:" + fullString.length + " end: " + subString.length
            )
            if (fullString.length > subString.length) {
                val s: Spannable = SpannableString(fullString)
                val start = subString.length + 1
                val end = fullString.length
                Log.e("CustomFunction", "updateNormaltoSubString start:$start end: $end")
                s.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                //        s.setSpan(new ForegroundColorSpan(Color.RED), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        s.setSpan(new BackgroundColorSpan(Color.YELLOW), 3, subString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        s.setSpan(new UnderlineSpan(), 4, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextView.setText(s, TextView.BufferType.SPANNABLE)
            }
        }

        fun hideSoftKeyboard(mContext: Context, mView: View) {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (mView is EditText) {
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0)
            } else if (mView is TextView) {
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0)
            }
        }

        fun showSoftKeyboard(mContext: Context, mView: View) {
//        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInputFromWindow(mView.windowToken, InputMethodManager.SHOW_FORCED, 0)
            if (mView is EditText) {
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0)
            } else if (mView is TextView) {
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0)
            }
        }

        fun showSoftKeyboardForcefully(mContext: Context) {
            (mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }

        fun printDifference(startDate: Date, endDate: Date): String {
            var time = ""

            //milliseconds
            var different = endDate.time - startDate.time
            Log.e("CustomFunction", "startDate : $startDate")
            Log.e("CustomFunction", "endDate : $endDate")
            Log.e("CustomFunction", "different : $different")
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val elapsedDays = different / daysInMilli
            different = different % daysInMilli
            val elapsedHours = different / hoursInMilli
            different = different % hoursInMilli
            val elapsedMinutes = different / minutesInMilli
            different = different % minutesInMilli
            var elapsedSeconds = different / secondsInMilli
            if (elapsedSeconds < 0) elapsedSeconds = 1
            time = if (elapsedDays > 0) {
                "$elapsedDays d"
            } else if (elapsedHours > 0) {
                "$elapsedHours h"
            } else if (elapsedMinutes > 0) "$elapsedMinutes m" else "$elapsedSeconds s"
            return time
        }

        fun localtime(utcTime: String?): String {
            var formattedDate = ""
            if (utcTime != null) {
                if (utcTime.length > 0) {
                    val unixSeconds = java.lang.Double.valueOf(utcTime).toLong()
                    val date =
                        Date(unixSeconds * 1000L) // 1000 is to convert seconds to milliseconds
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z") // the format of your date
                    //sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // give a timezone reference for formating (see comment at the bottom
                    formattedDate = sdf.format(date)
                }
            }
            return formattedDate
        }

        @SuppressLint("ClickableViewAccessibility")
        fun activateScrollToEditText(editText: EditText, parentScroll: NestedScrollView) {
            editText.setOnTouchListener { view, motionEvent ->
                parentScroll.requestDisallowInterceptTouchEvent(true)
                false
            }
        }

        fun getStringTimeFromLongMilliSecs(timeMs: Long): String {
            val mFormatBuilder = StringBuilder()
            val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
            val totalSeconds = timeMs / 1000
            //  videoDurationInSeconds = totalSeconds % 60;
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            mFormatBuilder.setLength(0)
            return if (hours > 0) {
                mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
            } else {
                mFormatter.format("%02d:%02d", minutes, seconds).toString()
            }
        }

        fun capitalizeWords(text: String): String {
            val words = text.split(" ").toTypedArray()
            val sb = StringBuilder()
            if (words[0].length > 0) {
                sb.append(
                    words[0][0].uppercaseChar().toString() + words[0].subSequence(
                        1,
                        words[0].length
                    ).toString().lowercase(
                        Locale.getDefault()
                    )
                )
                for (i in 1 until words.size) {
                    sb.append(" ")
                    sb.append(
                        words[i][0].uppercaseChar().toString() + words[i].subSequence(
                            1,
                            words[i].length
                        ).toString().lowercase(
                            Locale.getDefault()
                        )
                    )
                }
            }
            return sb.toString()
        }

        fun getExtrasBundleDetails(mIntent: Intent) {
            try {
                val b = mIntent.extras
                if (b != null) {
                    for (key in b.keySet()) {
                        Log.e(
                            "CustomFunction-->",
                            " getExtrasBundleDetails Key:" + key + " : " + if (b[key] != null) "Value:" + b[key] else "Value: NULL"
                        )
                    }
                } else {
                    Log.e("CustomFunction-->", " getExtrasBundleDetails Values: NULL")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getRawURL(url: String): String {
            try {
                return url.replace("/", "\\")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return url
        }

        fun formatValue(value: Double?): String {
//        Log.e("formatValue", "formatValue called Value: " + value);
            if (value == null || value == 0.0) {
//            Log.e("formatValue", "formatValue called returned Value: " + "0");
                return "0"
            }
            return if (value < 1.0) String.format(Locale.US, "%.2f", value) else String.format(
                Locale.US, "%" + (Math.log10(value)
                    .toInt() + 1) + ".0f", value
            )
        }

        fun getWeightHeight(text: String, unit: String): String {
            val value = text.toDouble()
            return "$value $unit"
        }

}