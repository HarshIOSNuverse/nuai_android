package com.checkmyself.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateFormatter {
    const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    const val yyyy = "yyyy"
    const val MMMM_d_yyyy = "MMMM d, yyyy"
    const val MMMM_d_yyyy_hh_mm_a = "MMMM d, yyyy, hh:mm a"

    //    private const val SERVER_STANDARD_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val SERVER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val dd_MM_yyyy_slash = "dd/MM/yyyy"
    const val SERVER_STANDARD_DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss"
    const val dd_MM_yyyy_SLASH = "dd/MM/yyyy"
    const val yyyy_MM_dd_DASH = "yyyy-MM-dd"
    private const val dd_MM_yyyy_DASH = "dd-MM-yyyy"
    const val dd_MM_yy_SLASH = "dd/MM/yy"
    const val MMM_yyyy_SPACE = "MMM, yyyy"
    const val MMM_yyyy_DASH = "MMM-yyyy"
    const val hh_mm_a_COLON = "hh:mm a"
    const val HH_mm_ss_COLON = "HH:mm:ss"
    const val EEEE = "EEEE"
    const val TIME_ZONE_UTC = "UTC"
    const val MMMM_yyyy = "MMMM yyyy"

//    @SuppressLint("SimpleDateFormat")
//    fun getFormattedDate(time: Long, outFormat: String): String {
//        val simpleDateFormat = SimpleDateFormat(outFormat)
//        return simpleDateFormat.format(time)
//    }

    fun getDateToString(outFormat: String, date: Date): String {
        val sdf = SimpleDateFormat(outFormat, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    fun getFormattedDate(inFormat: String, date: String, outFormat: String): String {
        var parsedDate: Date? = null
        try {
            parsedDate = SimpleDateFormat(inFormat, Locale.ENGLISH).parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return if (parsedDate != null)
            SimpleDateFormat(outFormat, Locale.ENGLISH).format(parsedDate)
        else
            date
    }

    fun getFormattedByString(inFormat: String, outFormat: String, strDate: String): String {
        var sdf = SimpleDateFormat(inFormat, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getDefault()/*getTimeZone(TIME_ZONE_UTC)*/
        return try {
            val date = sdf.parse(strDate)
            sdf = SimpleDateFormat(outFormat, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getDefault()
            sdf.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            " "
        }
    }

    fun getFormattedByStringTimestamp(
        dateFormat: String, timestamp: Long, timeZone: TimeZone
    ): String? {
        var outDate: String? = null
        try {
            val dateFormatter =
                SimpleDateFormat(dateFormat, Locale.ENGLISH)
            dateFormatter.timeZone = timeZone
            outDate = dateFormatter.format(Date(timestamp))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outDate
    }


    fun getFormattedDateUTCToLocal(inFormat: String, date: String, outFormat: String): String {
        var parsedDate: Date? = null
        try {
            val df = SimpleDateFormat(inFormat, Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            parsedDate = df.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return if (parsedDate != null) {
            val df = SimpleDateFormat(outFormat, Locale.ENGLISH)
            df.timeZone = TimeZone.getDefault()
            df.format(parsedDate)
        } else
            date
    }

    fun getDateTimeInUTC(inFormat: String, dateString: String, outFormat: String): String {
        val sdf = SimpleDateFormat(inFormat, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var date = Date()
        try {
            date = sdf.parse(dateString)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val engDate = SimpleDateFormat(outFormat, Locale.ENGLISH)
        engDate.timeZone = TimeZone.getDefault()

        return engDate.format(date)
    }

    fun getFormattedByStringUTC(inFormat: String, outFormat: String, strDate: String): String {
        var sdf = SimpleDateFormat(inFormat, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone(TIME_ZONE_UTC)
        return try {
            val date = sdf.parse(strDate)
            sdf = SimpleDateFormat(outFormat, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getDefault()
            sdf.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            " "
        }
    }


    fun getDateTimeInUTC(dateTime: Long, outFormat: String): String {
        val sdf = SimpleDateFormat(outFormat, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = Date(dateTime)
        return sdf.format(date)
    }

//    @SuppressLint("SimpleDateFormat")
//    fun getSimpleDateTimeFromServer(dateString: String, outFormat: String): String {
//        val sdf = SimpleDateFormat(SERVER_STANDARD_DATE_FORMAT, Locale.ENGLISH)
//        sdf.timeZone = TimeZone.getTimeZone("UTC")
//        var date = Date()
//        try {
//            date = sdf.parse(dateString)!!
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//
//        val engDate = SimpleDateFormat(outFormat)
//        engDate.timeZone = TimeZone.getDefault()
//
//        return engDate.format(date)
//    }

//    fun getDateFromString(inFormat: String, date: String): Date {
//        var parsedDate: Date? = null
//        try {
//            parsedDate = SimpleDateFormat(inFormat, Locale.ENGLISH).parse(date)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//
//        return parsedDate!!
//    }

//    fun getStringTimeFromTimeInMilliseconds(milliSeconds: Long, outFormat: String): String {
//        var time: String? = null
//        try {
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = milliSeconds
//            time = SimpleDateFormat(outFormat, Locale.ENGLISH).format(calendar.time)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return time!!
//    }

    fun getDuration(date: String): Long {
        val parsedDate: Date?
        val currentDate = Date()
        try {
            parsedDate = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.ENGLISH).parse(date)
            if (parsedDate != null) {
                return parsedDate.time - currentDate.time
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    fun getDateByString(dateFormat: String, stringDate: String, timeZone: TimeZone): Date? {
        var date: Date? = null
        try {
            date = getSimpleDateFormat(dateFormat, timeZone).parse(stringDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    private fun getSimpleDateFormat(format: String, timeZone: TimeZone): SimpleDateFormat {
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        simpleDateFormat.timeZone = timeZone
        return simpleDateFormat
    }
}
