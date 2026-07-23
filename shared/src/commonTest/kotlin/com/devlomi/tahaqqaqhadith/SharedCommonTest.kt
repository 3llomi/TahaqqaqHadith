package com.devlomi.tahaqqaqhadith

import com.devlomi.tahaqqaqhadith.data.network.parseSearchResponse
import com.devlomi.tahaqqaqhadith.data.parser.HadithHtmlParser
import com.devlomi.tahaqqaqhadith.data.parser.FakeHadithPageParser
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

    @Test
    fun parsesFakeHadithPageItemsWithOptionalAlternative() {
        val result = FakeHadithPageParser().parse(page = 3, rawContent = fakeHadithPageMarkdown)

        assertEquals(2, result.items.size)
        assertEquals(41, result.items[0].number)
        assertTrue(result.items[0].hadith.contains("البلاء موكل بالمنطق"))
        assertTrue(result.items[0].grade?.contains("لا يصح") == true)
        assertEquals("https://dorar.net/fake-hadith/41?alts=1", result.items[0].sahihAlternativeUrl)

        assertEquals(43, result.items[1].number)
        assertTrue(result.items[1].grade.isNullOrBlank())
        assertTrue(result.items[1].text.contains("الجنة تحت أقدام الأمهات"))
    }

    @Test
    fun parsesFakeHadithRawHtmlArticles() {
        val result = FakeHadithPageParser().parse(page = 3, rawContent = fakeHadithPageHtml)

        assertEquals(2, result.items.size)
        assertEquals(41, result.items[0].number)
        assertTrue(result.items[0].hadith.contains("البلاء موكل بالمنطق"))
        assertTrue(result.items[0].grade?.contains("لا يصح") == true)
        assertEquals("https://dorar.net/fake-hadith/41?alts=1", result.items[0].sahihAlternativeUrl)

        assertEquals(42, result.items[1].number)
        assertTrue(result.items[1].hadith.contains("التمس لأخيك"))
        assertTrue(result.items[1].grade?.contains("الحديث بهذا اللفظ") == true)
    }

    private val sampleJson = """
        {
          "ahadith": {
            "result": "<div class=\"hadith\">1 - بورك لأمتي في بكورها .</div><div class=\"hadith-info\"><span class=\"info-subtitle\">الراوي:</span> أنس بن مالك <span class=\"info-subtitle\">المحدث:</span> الذهبي <span class=\"info-subtitle\">المصدر:</span> ميزان الاعتدال <span class=\"info-subtitle\">الصفحة أو الرقم:</span> 3/171 <span class=\"info-subtitle\">خلاصة حكم المحدث:</span> ضعيف</div><div class=\"hadith\">2 - بورك لأمتي في بكورها .</div><div class=\"hadith-info\"><span class=\"info-subtitle\">الراوي:</span> عبدالله بن عمر <span class=\"info-subtitle\">المحدث:</span> الألباني <span class=\"info-subtitle\">المصدر:</span> صحيح الجامع <span class=\"info-subtitle\">الصفحة أو الرقم:</span> 2841 <span class=\"info-subtitle\">خلاصة حكم المحدث:</span> صحيح</div><a href=\"https://dorar.net/hadith/search?q=بكورها\">المزيد</a>"
          }
        }
    """.trimIndent()

    private val fakeHadithPageMarkdown = """
        Title: أحاديث منتشرة لا تصح

        Markdown Content:
        ##### 41 - حديث: ((البلاء موكل بالمنطق)).

        **الدرجة: لا يصح، وصحح معناه ابن القيم في تحفة المودود.****|[الصحيح البديل](https://dorar.net/fake-hadith/41?alts=1)**

        [](https://dorar.net/fake-hadith/41)[](https://dorar.net/fake-hadith/41 "عرض الحديث")
        ##### 43 - حديث: ((الجنة تحت أقدام الأمهات)).

         وفي لفظ: ((الجنة تحت أقدام الأمهات، مَنْ شِئن أدخلن، ومَن شِئن أخرجن!))

        [](https://dorar.net/fake-hadith/43)[](https://dorar.net/fake-hadith/43 "عرض الحديث")
    """.trimIndent()

    private val fakeHadithPageHtml = """
        <html><body>
        <article class="border-bottom py-4">
            <h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="41">
                41 - حديث: ((البلاء موكل بالمنطق)).
            </h5>
            <div class="d-block mb-2">
                <strong class="px-2">الدرجة:
                    <span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="41">لا يصح، وصحح معناه ابن القيم في «تحفة المودود».</span>
                </strong>
                <strong class="px-2">
                    <span class="text-danger">|</span>
                    <a class="mr-2 text-info" target="_blank" href="https://dorar.net/fake-hadith/41?alts=1">الصحيح البديل</a>
                </strong>
            </div>
            <a href="https://dorar.net/fake-hadith/41" title="عرض الحديث"></a>
        </article>
        <article class="border-bottom py-4">
            <h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="42">
                42 - حديث: ((التمس لأخيك بِضعًا وسبعين عُذرًا)).
            </h5>
            <div class="d-block mb-2">
                <strong class="px-2">الدرجة:
                    <span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="42">الحديث بهذا اللفظ لم نجدْه.</span>
                </strong>
            </div>
            <a href="https://dorar.net/fake-hadith/42" title="عرض الحديث"></a>
        </article>
        </body></html>
    """.trimIndent()
}