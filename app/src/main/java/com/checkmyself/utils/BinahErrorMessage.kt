package com.checkmyself.utils

import ai.binah.sdk.api.alerts.AlertCodes


object BinahErrorMessage {
    fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            AlertCodes.DEVICE_CODE_LOW_POWER_MODE_ENABLED_ERROR -> {
                "A session was requested to run while the device is in 'Battery Saver' mode."
            }

            AlertCodes.DEVICE_CODE_TORCH_UNAVAILABLE_ERROR -> {
                "A PPG session was requested to run, but the SDK could not activate the flashlight. It could be either because the device has no flashlight or due to a software or hardware issue."
            }

            AlertCodes.DEVICE_CODE_TORCH_SHUT_DOWN_ERROR -> {
                "The flashlight was shut down while a PPG session was running."
            }

            AlertCodes.DEVICE_CODE_MINIMUM_BATTERY_LEVEL_ERROR -> {
                "The battery level is below 20% and prevents accurate measurement."
            }

            AlertCodes.DEVICE_CODE_CLOCK_SKEW_ERROR -> {
                "Severe clock skew detected"
            }

            AlertCodes.CAMERA_CODE_NO_CAMERA_ERROR -> {
                "This error is returned when attempting to start a session but the device has no camera that matches the session (Required resolution of 640x480 or above, 30 FPS, rear camera requires a torch for PPG measurement)"
            }

            AlertCodes.CAMERA_CODE_CAMERA_OPEN_ERROR -> {
                "Could not open the camera"
            }

            AlertCodes.CAMERA_CODE_CAMERA_MISSING_PERMISSIONS_ERROR -> {
                "The application does not have permission to access the camera."
            }

            AlertCodes.CAMERA_CODE_UNEXPECTED_IMAGE_DIMENSIONS_WARNING -> {
                "The images received from the camera have a different resolution than the requested resolution."
            }


            AlertCodes.LICENSE_CODE_ACTIVATION_LIMIT_REACHED_ERROR -> {
                "No more devices can be used with your license"
            }

            AlertCodes.LICENSE_CODE_METER_ATTRIBUTE_USES_LIMIT_REACHED_ERROR -> {
                "Contact customer support."
            }

            AlertCodes.LICENSE_CODE_AUTHENTICATION_FAILED_ERROR -> {
                "Several issues might cause this error: clock skew detected, the SDK was unable to authenticate the license, a bad token has been received from the license server"
            }

            AlertCodes.LICENSE_CODE_INVALID_LICENSE_KEY_ERROR -> {
                "The provided license key is invalid"
            }

            AlertCodes.LICENSE_CODE_NETWORK_ISSUES_ERROR -> {
                "Network issue prevents validating the license with the license server."
            }

            AlertCodes.LICENSE_CODE_SSL_HANDSHAKE_ERROR -> {
                "Certificate issue. It is possible that the device date/time mismatch the actual date/time"
            }

            AlertCodes.LICENSE_CODE_INPUT_PRODUCT_ID_ILLEGAL_ERROR, AlertCodes.LICENSE_CODE_CANNOT_OPEN_FILE_FOR_READ_ERROR -> {
                "Internal error"
            }

            AlertCodes.LICENSE_CODE_INPUT_LICENSE_KEY_EMPTY_ERROR -> {
                "No license key was provided to SDK."
            }

            AlertCodes.LICENSE_CODE_MONTHLY_USAGE_TRACKING_REQUIRES_SYNC_ERROR -> {
                "he SDK failed to authenticate with the license server as required by the license type."
            }

            AlertCodes.LICENSE_CODE_SSL_HANDSHAKE_DEVICE_DATE_ERROR -> {
                "SSL certificate security warning."
            }

            AlertCodes.LICENSE_CODE_SSL_HANDSHAKE_CERTIFICATE_EXPIRED_ERROR -> {
                "SSL certificate security warning."
            }

            AlertCodes.LICENSE_CODE_MIN_SDK_ERROR -> {
                "The SDK version is too old to be used with this license."
            }

            2041 -> {
                "The license type is forbidden from being used with the current SDK type. (Probably \"annual active users\" license with Web SDK.)"
            }

            AlertCodes.LICENSE_CODE_NETWORK_TIMEOUT_ERROR -> {
                "Network timeout reached for a single call."
            }

            AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_ERROR -> {
                "The face or finger was not detected for a period of over 0.5 second more than 2 times"
            }

            AlertCodes.MEASUREMENT_CODE_INVALID_RECENT_DETECTION_RATE_ERROR -> {
                "The face or finger were not detected for a period of over 0.5 seconds several times."
            }

            AlertCodes.MEASUREMENT_CODE_LICENSE_ACTIVATION_FAILED_ERROR -> {
                "The license activation failed. For example due to tampered license file or proxy misconfiguration."
            }

            AlertCodes.MEASUREMENT_CODE_TOO_MANY_FRAMES_INORDER_ERROR -> {
                "Multiple consecutive frames were received in incorrect timestamp order. This error occurs if warning 3506 is issued multiple times."
            }

            AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                "The face or finger was not detected for a period of over 0.5 second."
            }

            AlertCodes.MEASUREMENT_CODE_INVALID_RECENT_FPS_RATE_WARNING -> {
                "Camera FPS is degraded and may affect the measurement quality."
            }

            AlertCodes.MEASUREMENT_CODE_MEASUREMENT_MISPLACED_FRAME_WARNING -> {
                "A frame was received in incorrect timestamp order."
            }

            AlertCodes.VITAL_SIGN_CODE_MEASURING_WITH_NO_ENABLED_VITAL_SIGNS_WARNING -> {
                "No vital signs were processed as part of this measurement."
            }

            AlertCodes.SESSION_CODE_ILLEGAL_START_CALL_ERROR -> {
                "A session start call was made when the session is not in READY state."
            }

            AlertCodes.SESSION_CODE_ILLEGAL_STOP_CALL_ERROR -> {
                "A session stop call was made when the session is not in PROCESSING state."
            }

            AlertCodes.INITIALIZATION_CODE_INVALID_PROCESSING_TIME_ERROR -> {
                "An invalid session time was provided when creating a session.."
            }

            AlertCodes.INITIALIZATION_CODE_ROTATION_AND_ORIENTATION_MISMATCH -> {
                "Both the withImageRotation and withDeviceOrientation properties were used when creating a session."
            }

            AlertCodes.INITIALIZATION_CODE_INVALID_LICENSE_FORMAT -> {
                "The provided license key is either empty or its format is invalid."
            }

            AlertCodes.INITIALIZATION_CODE_SDK_LOAD_FAILURE -> {
                "The SDK failed to initialize, probably due to unsupported hardware."
            }

            AlertCodes.INITIALIZATION_CODE_UNSUPPORTED_USER_WEIGHT -> {
                "The weight submitted by the user is not supported. The supported weight range is between 40 to 200 kilograms."
            }

            AlertCodes.INITIALIZATION_CODE_UNSUPPORTED_USER_AGE -> {
                "The age submitted by the user is not supported. The supported age range is between 18 to 110 years."
            }

            AlertCodes.INITIALIZATION_CODE_CONCURRENT_SESSIONS_ERROR -> {
                "Trying to create a new session before terminating the previous session."
            }

            AlertCodes.SENSOR_CODE_UNSUPPORTED_SENSOR_FIRMWARE_VERSION -> {
                "The Bluetooth device firmware version is not supported."
            }

            else -> {
                ""
            }
        }
    }
}