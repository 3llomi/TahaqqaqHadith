package com.devlomi.tahaqqaqhadith

import com.devlomi.tahaqqaqhadith.data.parser.FakeHadithPageParser
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedLogicAndroidHostTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun parsesRealFakeHadithHtmlFixture() {
        val projectRoot = File(System.getProperty("user.dir") ?: ".")
        val fixture = File(projectRoot, "fake-hadith-sample.html")
        if (!fixture.exists()) return

        val rawHtml = fixture.readText()
        val result = FakeHadithPageParser().parse(page = 3, rawContent = rawHtml)

        assertTrue(result.items.size >= 15)
        assertEquals(31, result.items.first().number)
        assertEquals(45, result.items.last().number)
        assertTrue(result.items.any { it.sahihAlternativeUrl?.contains("alts=1") == true })
    }
}