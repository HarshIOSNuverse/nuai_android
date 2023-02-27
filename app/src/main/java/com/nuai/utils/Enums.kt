package com.nuai.utils

object Enums {
    const val Y = "Y"
    const val N = "N"

    enum class Language constructor(internal var language: String) {
        ENGLISH("en"), ARABIC("ar");

        override fun toString(): String {
            return language
        }
    }

    enum class Gender {
        MALE, FEMALE, OTHER
    }

    enum class ScanType {
        FACE, FINGER
    }

    enum class WellnessScore {
        LOW, HIGH
    }

    enum class SessionMode {
        FACE, FINGER
    }

    enum class UiState {
        LOADING, MEASURING, IDLE, MANUALLY_STOPPED, MEASUREMENT_COMPLETED, SCREEN_PAUSED, SCREEN_RESUMED
    }

    enum class ResultCategory {
        VITAL_SIGNS, BLOOD, STRESS_LEVEL, ENERGY, HEART_RATE_VARIABILITY
    }

    enum class ResultFrom {
        HEART_RATE, HRV_SDNN, STRESS_LEVEL, RECOVERY_ABILITY, STRESS_RESPONSE, OXYGEN_SATURATION,
        PRQ, BREATH_RATE, BLOOD_PRESSURE
    }

    enum class ResultType constructor(internal var type: String) {
        HEART_RATE("Heart Rate"), HRV_SDNN("HRV-SDNN"), STRESS_LEVEL("Stress Level"), RECOVERY_ABILITY("Recovery Ability"),
        STRESS_RESPONSE("Stress Response"), OXYGEN_SATURATION("Oxygen Saturation"), PRQ("PRQ"),
        BREATH_RATE("Breathing Rate"), BLOOD_PRESSURE("Blood Pressure"),HEMOGLOBIN("Hemoglobin"),
        HEMOGLOBIN_A1C("Hemoglobin A1c");

        override fun toString(): String {
            return type
        }
    }
}