package com.devlomi.tahaqqaqhadith.common.util

object Util{
    //remove «, » , ., and tashkeel
    fun sanitizeHadithText(text: String): String {
        val regex = "[«».,\\u064B-\\u065F]".toRegex()
        return text.replace(regex, "").trim()
    }
    private fun removeTashkeel(text: String): String {
        // Replaces all Arabic diacritics/tashkeel with an empty string
        val regex = "[\\u064B-\\u065F]".toRegex()
        return text.replace(regex, "")
    }
}