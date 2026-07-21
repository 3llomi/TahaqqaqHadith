package com.devlomi.tahaqqaqhadith.data.parser

import com.devlomi.tahaqqaqhadith.data.model.HadithEntry
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult
import com.devlomi.tahaqqaqhadith.domain.LegitimacyScorer

class HadithHtmlParser(
    private val scorer: LegitimacyScorer = LegitimacyScorer()
) {

    fun parse(query: String, html: String): HadithSearchResult {
        val entries = hadithBlockRegex.findAll(html).mapIndexed { index, match ->
            val hadithHtml = match.groupValues[1]
            val infoHtml = match.groupValues[2]

            val hadithText = stripHtml(hadithHtml)
                .replace(leadingNumberRegex, "")
                .normalizeSpaces()

            val verdict = extractInfoField(infoHtml, "خلاصة حكم المحدث:")

            HadithEntry(
                index = index + 1,
                hadithText = hadithText,
                narrator = extractInfoField(infoHtml, "الراوي:"),
                scholar = extractInfoField(infoHtml, "المحدث:"),
                source = extractInfoField(infoHtml, "المصدر:"),
                pageOrNumber = extractInfoField(infoHtml, "الصفحة أو الرقم:"),
                verdict = verdict,
                assessment = scorer.scoreVerdict(verdict)
            )
        }.toList()

        val moreUrl = moreLinkRegex.find(html)?.groupValues?.getOrNull(1)
        return HadithSearchResult(
            query = query,
            entries = entries,
            overallAssessment = scorer.scoreOverall(entries),
            moreUrl = moreUrl
        )
    }

    private fun extractInfoField(infoHtml: String, label: String): String {
        val regex = Regex(
            pattern = """(?s)<span class="info-subtitle">\s*${Regex.escape(label)}\s*</span>\s*(.*?)(?=<span class="info-subtitle">|$)""",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        val raw = regex.find(infoHtml)?.groupValues?.getOrNull(1).orEmpty()
        return stripHtml(raw).normalizeSpaces()
    }

    private fun stripHtml(text: String): String {
        return text
            .replace("<br/>", " ")
            .replace("<br>", " ")
            .replace(tagRegex, " ")
            .replace("&nbsp;", " ")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&#39;", "'")
    }

    private fun String.normalizeSpaces(): String =
        replace("\\s+".toRegex(), " ").trim()

    companion object {
        private val hadithBlockRegex = Regex(
            pattern = """(?s)<div class="hadith"[^>]*>(.*?)</div>\s*<div class="hadith-info">(.*?)</div>""",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        private val moreLinkRegex = Regex(
            pattern = """<a href="([^"]+)">\s*المزيد\s*</a>""",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        private val leadingNumberRegex = Regex("^\\d+\\s*-\\s*")
        private val tagRegex = Regex("<[^>]+>")
    }
}


