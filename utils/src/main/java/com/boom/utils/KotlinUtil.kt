package com.boom.utils;

class KotlinUtil {
    companion object {
        /**
         * Kotlin trim method will remove the full width space but java's trim is not.
         */
        @JvmStatic
        fun trim(text:String):String {
            return text.trim()
        }
    }
}