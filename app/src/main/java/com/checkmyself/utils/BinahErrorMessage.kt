package com.checkmyself.utils

import ai.binah.hrv.api.HealthMonitorCodes

object BinahErrorMessage {
    fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            HealthMonitorCodes.DEVICE_CODE_LOW_POWER_MODE_ENABLED_ERROR -> {
                "This error is returned when trying to run measurement and the device is in \"low power mode\" (\"Battery Saver Mode\"). This mode is enabled/disabled via settings."
            }
            HealthMonitorCodes.DEVICE_CODE_TORCH_UNAVAILABLE_ERROR -> {
                "This error happens when attempting to start a PPG measurement but the operating system fails to operate the torch. Could be either because the device has no torch or due to a software or hardware issue."
            }
            HealthMonitorCodes.DEVICE_CODE_TORCH_SHUT_DOWN_ERROR -> {
                "This error happens when the torch shut down during a PPG measurement."
            }
            HealthMonitorCodes.DEVICE_CODE_INTERNAL_ERROR_1 -> {
                "Internal error when a file can not be opened for reading."
            }
            HealthMonitorCodes.DEVICE_CODE_INTERNAL_ERROR_2 -> {
                "Internal error when a file cannot be opened for writing"
            }
            HealthMonitorCodes.DEVICE_CODE_INTERNAL_ERROR_3,
            HealthMonitorCodes.DEVICE_CODE_INTERNAL_ERROR_4-> {
                "Internal error"
            }
            HealthMonitorCodes.DEVICE_CODE_MINIMUM_BATTERY_LEVEL_ERROR -> {
                "The battery level is below 20% and prevents accurate measurement."
            }
            HealthMonitorCodes.DEVICE_CODE_CLOCK_SKEW_ERROR -> {
                "Severe clock skew detected"
            }
            HealthMonitorCodes.CAMERA_CODE_NO_CAMERA_ERROR -> {
                "This error is returned when attempting to start a session but the device has no camera that matches the session (Required resolution of 640x480 or above, 30 FPS, rear camera requires a torch for PPG measurement)"
            }
            HealthMonitorCodes.CAMERA_CODE_CAMERA_OPEN_ERROR -> {
                "Could not open the camera"
            }
            HealthMonitorCodes.CAMERA_CODE_CAMERA_MISSING_PERMISSIONS_ERROR -> {
                "The application does not have permissions from OS to open the camera"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_18 -> {
                "Could not activate the license on the device."
            }
            HealthMonitorCodes.LICENSE_CODE_ACTIVATION_LIMIT_REACHED_ERROR -> {
                "No more devices can be used with your license"
            }
            HealthMonitorCodes.LICENSE_CODE_METER_ATTRIBUTE_USES_LIMIT_REACHED_ERROR -> {
                "Contact Binah.ai customer support."
            }
            HealthMonitorCodes.LICENSE_CODE_AUTHENTICATION_FAILED_ERROR -> {
                "Several issues might cause this error: clock skew detected, the SDK was unable to authenticate the license, a bad token has been received from the license server"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_1 -> {
                "The provided product Id is invalid. Product ID is set internally."
            }
            HealthMonitorCodes.LICENSE_CODE_INVALID_LICENSE_KEY_ERROR -> {
                "The provided license key is invalid"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_2 -> {
                "The provided license key is invalid"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_3 -> {
                "The SDK is executed within a virtual machine. This mode is not supported."
            }
            HealthMonitorCodes.LICENSE_CODE_REVOKED_LICENSE_ERROR -> {
                "The license was revoked."
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_4 -> {
                "Internal error when the country of the current device IP addresses is blocked according to license. By default all countries are allowed."
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_5 -> {
                "Internal error when the current device IP is blocked according to license. By default all IP addresses are allowed."
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_7,
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_8-> {
                "Trial license error"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_9 -> {
                "SSL error. Unable to authenticate license server response."
            }
            HealthMonitorCodes.LICENSE_CODE_LICENSE_EXPIRED_ERROR -> {
                "The license is expired."
            }
            HealthMonitorCodes.LICENSE_CODE_LICENSE_SUSPENDED_ERROR -> {
                "The license is suspended"
            }
            HealthMonitorCodes.LICENSE_CODE_TOKEN_EXPIRED_ERROR -> {
                "The local token on the device is expired and a new license token is required. To simulate after successfully completing a measurement move your device local time ahead of the grace period and issue another measurement"
            }
            HealthMonitorCodes.LICENSE_CODE_DEVICE_DEACTIVATED_ERROR -> {
                "The device is activated but has been deleted from the license server."
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_10 -> {
                "Internal error. License configuration error"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_11 -> {
                "Internal license error"
            }
            HealthMonitorCodes.LICENSE_CODE_NETWORK_ISSUES_ERROR -> {
                "Network issue prevents validating the license with the Binah license server."
            }
            HealthMonitorCodes.LICENSE_CODE_SSL_HANDSHAKE_ERROR -> {
                "Certificate issue. It is possible that the device date/time mismatch the actual date/time"
            }
            HealthMonitorCodes.LICENSE_CODE_INTERNAL_ERROR_16,
            HealthMonitorCodes.LICENSE_CODE_INPUT_FINGERPRINT_EMPTY_ERROR,
            HealthMonitorCodes.LICENSE_CODE_INPUT_PRODUCT_ID_ILLEGAL_ERROR,
            HealthMonitorCodes.LICENSE_CODE_CANNOT_OPEN_FILE_FOR_READ_ERROR-> {
                "Internal error"
            }
            HealthMonitorCodes.LICENSE_CODE_INPUT_LICENSE_KEY_EMPTY_ERROR -> {
                "No license key was provided to SDK."
            }
            HealthMonitorCodes.LICENSE_CODE_MONTHLY_USAGE_TRACKING_REQUIRES_SYNC_ERROR -> {
                "Returned on licenses from type \"Pay as You Go\" when the device fails to sync once a month"
            }
            HealthMonitorCodes.LICENSE_CODE_SSL_HANDSHAKE_DEVICE_DATE_ERROR -> {
                "SSL error, could be because the device date is incorrect"
            }
            HealthMonitorCodes.LICENSE_CODE_SSL_HANDSHAKE_CERTIFICATE_EXPIRED_ERROR -> {
                "SSL error"
            }
            HealthMonitorCodes.LICENSE_CODE_MIN_SDK_ERROR -> {
                "The license is not supported in this SDK version"
            }
            HealthMonitorCodes.LICENSE_CODE_MISSING_SDK_VERSION -> {
                "The SDK version is missing in SDK. Probably due to an integrity error."
            }
            2041 -> {
                "The license type is forbidden from being used with the current SDK type. (Probably \"annual active users\" license with Web SDK.)"
            }
            HealthMonitorCodes.LICENSE_CODE_NETWORK_TIMEOUT_ERROR -> {
                "Timeout on a single network call"
            }
            HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_ERROR -> {
                "The face or finger was not detected for a period of over 0.5 second more than 2 times"
            }
            HealthMonitorCodes.MEASUREMENT_CODE_INVALID_RECENT_DETECTION_RATE_ERROR -> {
                "Too many frame losses detected over 2 times during the measurement."
            }
            HealthMonitorCodes.MEASUREMENT_CODE_LICENSE_ACTIVATION_FAILED_ERROR -> {
                "The license activation failed. For example due to tampered license file or proxy misconfiguration."
            }
            HealthMonitorCodes.MEASUREMENT_CODE_INVALID_MEASUREMENT_AVERAGE_DETECTION_RATE_ERROR -> {
                "Average detection rate is significantly lower than expected. Could be because the device is busy, overloaded, overheated or any other hardware operating system software or hardware issue."
            }
            3009 -> {
                "Many concecutive frames were received in incorrect order according to their timestamp. This error is received if warning 3506 repeated multiple times."
            }
            HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                "The face or finger was not detected for a period of over 0.5 second."
            }
            HealthMonitorCodes.MEASUREMENT_CODE_UNSUPPORTED_ORIENTATION_WARNING -> {
                "The device is rotated"
            }
            else -> {
                ""
            }
        }
    }
}