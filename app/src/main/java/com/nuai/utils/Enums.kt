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

}