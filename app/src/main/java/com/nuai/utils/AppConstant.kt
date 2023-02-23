package com.nuai.utils

/*
    This is a place where all the constants used in app are defined here
 */
object AppConstant {
    const val BINAH_AI_SCANNING_TIME_SECONDS:Long = 70
    var RESULT_SCREEN_DELAY_TIME = 1500
    var PLATFORM_ANDROID = "Android"

    // login, Sing up, forgot password,
    var EXTRA_SCANNED_RESULT_ID = "show_face_measurement_result_id"
    var EXTRA_SCAN_RESULT = "show_face_measurement_result"
    var EXTRA_EMAIL = "email"

    // result screen remove result
    var EXTRA_SCAN_RESULT_DELETED = "scan_result_deleted"
    var INFO_DIALOG_KEY_HEART = "heart_rate"
    var INFO_DIALOG_KEY_OXYGEN = "oxygen_saturation"
    var INFO_DIALOG_KEY_RESPIRATION = "respiration"
    var INFO_DIALOG_KEY_STRESS = "stress_five_levels"
    var INFO_DIALOG_KEY_HRV_SDNN = "hrv_sdnn"
    var INFO_DIALOG_KEY_BODY_MASS = "body_mass"
    var INFO_DIALOG_KEY_BLOOD_PRESSURE = "blood-pressure"
    const val FORMAT = "%02d:%02d"
    const val WEB_DETAIL_TAG = "webDetailTag"
    const val WEB_DETAIL_TAG_DISCLAIMER = "disclaimer"
    const val WEB_DETAIL_TAG_HEART = "heart-rate-bpm"
    const val WEB_DETAIL_TAG_OXYGEN = "oxygen-saturation-spo2"
    const val WEB_DETAIL_TAG_RESPIRATION = "respiration-rpm"
    const val WEB_DETAIL_TAG_HEMOGLOBIN = "hemoglobin"
    const val WEB_DETAIL_TAG_HBA1C = "hba1c"
    const val WEB_DETAIL_TAG_STRESS = "stress-level-5-levels"
    const val WEB_DETAIL_TAG_HRV_SDNN = "hrv-sdnn"
    const val WEB_DETAIL_TAG_BODY_MASS_INDEX = "body-mass-index-bmi"
    const val WEB_DETAIL_TAG_BLOOD_PRESSURE = "blood-pressure"
    var SERVICE_LOCATION_REQUEST_CODE = 105
    var SERVICE_NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID_SERVICE = "1000"
    var PURCHASE_MONTH = 1
    var PURCHASE_YEAR = 0
}