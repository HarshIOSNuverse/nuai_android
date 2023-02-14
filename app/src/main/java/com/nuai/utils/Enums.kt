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
        MALE, FEMALE
    }
}