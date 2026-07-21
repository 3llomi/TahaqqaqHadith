package com.devlomi.tahaqqaqhadith

import com.devlomi.tahaqqaqhadith.data.network.parseSearchResponse
import com.devlomi.tahaqqaqhadith.data.parser.HadithHtmlParser
import com.devlomi.tahaqqaqhadith.data.model.LegitimacyState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedCommonTest {

    @Test
    fun parsesEmbeddedHtmlIntoEntries() {
        val response = parseSearchResponse(sampleJson)
        val result = HadithHtmlParser().parse(query = "بكورها", html = response.ahadith.result)

        assertEquals(2, result.entries.size)
        assertTrue(result.entries.first().hadithText.contains("بورك"))
        assertEquals("أنس بن مالك", result.entries.first().narrator)
        assertEquals("صحيح", result.entries.last().verdict)
    }

    @Test
    fun computesExpectedOverallState() {
        val response = parseSearchResponse(sampleJson)
        val result = HadithHtmlParser().parse(query = "بكورها", html = response.ahadith.result)

        assertEquals(LegitimacyState.NEEDS_REVIEW, result.overallAssessment.state)
        assertTrue(result.overallAssessment.score in 0..100)
    }

    private val sampleJson = """
        {
          "ahadith": {
            "result": "<div class=\"hadith\">1 - بورك لأمتي في بكورها .</div><div class=\"hadith-info\"><span class=\"info-subtitle\">الراوي:</span> أنس بن مالك <span class=\"info-subtitle\">المحدث:</span> الذهبي <span class=\"info-subtitle\">المصدر:</span> ميزان الاعتدال <span class=\"info-subtitle\">الصفحة أو الرقم:</span> 3/171 <span class=\"info-subtitle\">خلاصة حكم المحدث:</span> ضعيف</div><div class=\"hadith\">2 - بورك لأمتي في بكورها .</div><div class=\"hadith-info\"><span class=\"info-subtitle\">الراوي:</span> عبدالله بن عمر <span class=\"info-subtitle\">المحدث:</span> الألباني <span class=\"info-subtitle\">المصدر:</span> صحيح الجامع <span class=\"info-subtitle\">الصفحة أو الرقم:</span> 2841 <span class=\"info-subtitle\">خلاصة حكم المحدث:</span> صحيح</div><a href=\"https://dorar.net/hadith/search?q=بكورها\">المزيد</a>"
          }
        }
    """.trimIndent()
}