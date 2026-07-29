package com.devlomi.tahaqqaqhadith.data.parser

import co.touchlab.kermit.Logger
import com.devlomi.tahaqqaqhadith.BASE_URL
import com.devlomi.tahaqqaqhadith.data.model.FakeHadith
import com.devlomi.tahaqqaqhadith.data.model.FakeHadithPageResult

class FakeHadithPageParser {

    fun parse(page: Int, rawContent: String): FakeHadithPageResult {
        val sourceUrl = "https://dorar.net/fake-hadith?page=$page"
        val markdownBody = rawContent.substringAfter("Markdown Content:", rawContent)

        val markdownItems = parseMarkdownStyle(markdownBody)
        val htmlItems = parseRawHtmlArticles(rawContent).ifEmpty {
            parseHtmlLike(rawContent)
        }

        val items = chooseBestItems(
            rawContent = rawContent,
            markdownItems = markdownItems,
            htmlItems = htmlItems
        )

        return FakeHadithPageResult(
            page = page,
            sourceUrl = sourceUrl,
            items = items.sortedBy { it.number }
        )
    }

    private fun parseMarkdownStyle(markdown: String): List<FakeHadith> {
        val headingRegex = Regex("""(?m)^#{3,6}\s*(\d+)\s*-\s*حديث\s*:\s*(.+?)\s*$""")
        val matches = headingRegex.findAll(markdown).toList()
        if (matches.isEmpty()) return emptyList()

        return matches.mapNotNull { match ->
            val start = match.range.first
            val end = matches.nextStartAfter(match, markdown.length)
            val block = markdown.substring(start, end)

            val number = match.groupValues[1].toIntOrNull() ?: return@mapNotNull null
            val heading = normalizeText(cleanTitle(match.groupValues[2]))
            if (!isLikelyHadithText(heading)) return@mapNotNull null

            val grade = gradeRegex.find(block)?.groupValues?.getOrNull(1)?.let(::normalizeText)
            val sahihAltUrl = sahihAlternativeRegex.find(block)?.groupValues?.getOrNull(1)
            val hadithUrl = emptyLinkToHadithRegex.find(block)?.groupValues?.getOrNull(1)

            val extraLines = block
                .lineSequence()
                .drop(1)
                .map(String::trim)
                .filter { it.isNotBlank() }
                .filterNot { it.startsWith("**الدرجة:") }
                .filterNot { it.startsWith("[](") }
                .joinToString(" ")
                .let(::normalizeText)

            val text = listOf(heading, extraLines)
                .filter { it.isNotBlank() }
                .distinct()
                .joinToString("\n\n")

            FakeHadith(
                number = number,
                hadith = heading,
                text = text,
                grade = grade,
                sahihAlternativeUrl = sahihAltUrl,
                hadithUrl = hadithUrl
            )
        }
    }

    private fun parseRawHtmlArticles(html: String): List<FakeHadith> {
        val articleMatches = articleRegex.findAll(html).toList()
        if (articleMatches.isEmpty()) return emptyList()

        return articleMatches.mapNotNull { articleMatch ->
            val article = articleMatch.groupValues[1]
            val hadithHtml = hadithH5Regex.find(article)?.groupValues?.getOrNull(1) ?: return@mapNotNull null
            val rawHadith = normalizeText(stripHtml(hadithHtml))

            val number = numberRegex.find(rawHadith)?.groupValues?.getOrNull(1)?.toIntOrNull()
                ?: dataPkRegex.find(article)?.groupValues?.getOrNull(1)?.toIntOrNull()
                ?: return@mapNotNull null

            val hadith = normalizeText(
                rawHadith
                    .replace(leadingHadithPrefixRegex, "")
                    .trim()
            )

            if (!isLikelyHadithText(hadith)) return@mapNotNull null

            val grade = degreeSpanRegex.find(article)?.groupValues?.getOrNull(1)
                ?.let(::stripHtml)
                ?.let(::normalizeText)


            val sahihAltUrl = extractHtmlSahihAlternativeUrl(article)
            val hadithUrl = htmlHadithUrlRegex(number).find(article)?.groupValues?.getOrNull(1)

            FakeHadith(
                number = number,
                hadith = hadith,
                text = hadith,
                grade = grade,
                sahihAlternativeUrl = sahihAltUrl,
                hadithUrl = hadithUrl
            )
        }
    }

    private fun parseHtmlLike(html: String): List<FakeHadith> {
        val normalized = html
            .replace(scriptTagRegex, " ")
            .replace(styleTagRegex, " ")
            .replace(htmlCommentRegex, " ")
            .replace("<br>", "\n")
            .replace("<br/>", "\n")
            .replace("<br />", "\n")

        val entryRegex = Regex(
            pattern = """(?s)(\d{1,4})\s*-\s*حديث\s*:\s*(.*?)(?=(?:\d{1,4}\s*-\s*حديث\s*:)|$)""",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        return entryRegex.findAll(normalized).mapNotNull { match ->
            val number = match.groupValues[1].toIntOrNull() ?: return@mapNotNull null
            val block = match.groupValues[2]

            val grade = htmlGradeRegex.find(block)?.groupValues?.getOrNull(1)?.let(::stripHtml)?.let(::normalizeText)
            val rawHadithText = block
                .substringBefore("الدرجة")
                .substringBefore("الصحيح البديل")
            val hadith = rawHadithBodyRegex.find(rawHadithText)?.groupValues?.getOrNull(1)
                ?.let(::normalizeText)
                ?: normalizeText(stripHtml(rawHadithText))

            if (!isLikelyHadithText(hadith)) return@mapNotNull null

            val sahihAltUrl = extractHtmlSahihAlternativeUrl(block)
            val hadithUrl = htmlHadithUrlRegex(number).find(block)?.groupValues?.getOrNull(1)

            FakeHadith(
                number = number,
                hadith = hadith,
                text = hadith,
                grade = grade,
                sahihAlternativeUrl = sahihAltUrl,
                hadithUrl = hadithUrl
            )
        }.toList()
    }

    private fun chooseBestItems(
        rawContent: String,
        markdownItems: List<FakeHadith>,
        htmlItems: List<FakeHadith>
    ): List<FakeHadith> {
        val looksLikeRawHtml = "<html" in rawContent.lowercase() || "<article" in rawContent.lowercase()
        val looksLikeMarkdown = "Markdown Content:" in rawContent || rawContent.contains("#####")

        if (looksLikeRawHtml && htmlItems.isNotEmpty()) return htmlItems
        if (looksLikeMarkdown && markdownItems.isNotEmpty()) return markdownItems

        val markdownScore = qualityScore(markdownItems)
        val htmlScore = qualityScore(htmlItems)

        return if (htmlScore >= markdownScore) htmlItems else markdownItems
    }

    private fun qualityScore(items: List<FakeHadith>): Int {
        if (items.isEmpty()) return 0
        val withGrade = items.count { !it.grade.isNullOrBlank() }
        val withUrl = items.count { it.hadithUrl?.contains("/fake-hadith/") == true }
        val withAlt = items.count { it.sahihAlternativeUrl?.contains("alts=1") == true }
        return (items.size * 4) + (withGrade * 2) + withUrl + withAlt
    }

    private fun cleanTitle(value: String): String {
        return value
            .removePrefix("حديث:")
            .removePrefix("حديث :")
            .trim()
            .removePrefix("((")
            .removeSuffix(")).")
            .removeSuffix("))")
            .trim()
    }

    private fun isLikelyHadithText(value: String): Boolean {
        if (value.length < 12) return false
        if (!arabicLetterRegex.containsMatchIn(value)) return false

        val lower = value.lowercase()
        if (suspiciousTokens.any { token -> token in lower }) return false

        return true
    }

    private fun stripHtml(text: String): String {
        return text
            .replace("<br>", "\n")
            .replace("<br/>", "\n")
            .replace("<br />", "\n")
            .replace(tagRegex, " ")
            .replace("&nbsp;", " ")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&#39;", "'")
    }

    private fun extractHtmlSahihAlternativeUrl(htmlBlock: String): String? {
        val labeledMatch = htmlSahihAlternativeRegex.find(htmlBlock)
        val prefix = "https://dorar.net"
        if (labeledMatch != null) {
            return labeledMatch.groupValues[1].let { prefix+it }
        }

        // Fallback for markup variants where the label text changes but `alts=1` remains stable.
        return htmlAlternativeByQueryRegex.find(htmlBlock)?.groupValues?.getOrNull(1)?.let { prefix+it }
    }

    private fun normalizeText(text: String): String = text
        .replace("\u00A0", " ")
        .replace("\\s+".toRegex(), " ")
        .replace(" .", ".")
        .trim()

    private fun List<MatchResult>.nextStartAfter(current: MatchResult, defaultEnd: Int): Int {
        val currentIndex = indexOf(current)
        if (currentIndex == -1 || currentIndex + 1 >= size) return defaultEnd
        return this[currentIndex + 1].range.first
    }

    companion object {
        private val gradeRegex = Regex("""\*\*الدرجة:\s*(.+?)\*\*""", RegexOption.DOT_MATCHES_ALL)
        private val sahihAlternativeRegex = Regex("""\[\s*الصحيح البديل\s*]\((https?://[^)\s]+)\)""")
        private val emptyLinkToHadithRegex = Regex("""\[\]\((https?://dorar\.net/fake-hadith/\d+[^)]*)\)""")

        private val htmlGradeRegex = Regex("""الدرجة\s*:\s*(.*?)(?:<|$)""", RegexOption.DOT_MATCHES_ALL)
        private val articleRegex = Regex("""(?is)<article\b[^>]*>(.*?)</article>""")
        private val hadithH5Regex = Regex("""(?is)<h5\b[^>]*data-name\s*=\s*"hadith"[^>]*>(.*?)</h5>""")
        private val degreeSpanRegex = Regex("""(?is)<span\b[^>]*data-name\s*=\s*"degree"[^>]*>(.*?)</span>""")
        private val numberRegex = Regex("""^(\d{1,4})\s*-""")
        private val dataPkRegex = Regex("""data-pk\s*=\s*"(\d{1,4})""", RegexOption.IGNORE_CASE)
        private val leadingHadithPrefixRegex = Regex("""^\d{1,4}\s*-\s*(?:حديث\s*:\s*)?""")
        private val htmlSahihAlternativeRegex = Regex(
            """(?is)<a\b[^>]*href\s*=\s*"([^"]+)"[^>]*>\s*(?:<[^>]+>\s*)*الصحيح\s+البديل""",
            RegexOption.IGNORE_CASE
        )
        private val htmlAlternativeByQueryRegex = Regex(
            """(?is)<a\b[^>]*href\s*=\s*"([^"]*\balts=1[^\"]*)""",
            RegexOption.IGNORE_CASE
        )
        private fun htmlHadithUrlRegex(number: Int) = Regex("""href\s*=\s*"([^"]*fake-hadith/$number[^"]*)""", RegexOption.IGNORE_CASE)

        private val rawHadithBodyRegex = Regex("""\(\((.*?)\)\)""", RegexOption.DOT_MATCHES_ALL)
        private val scriptTagRegex = Regex("""(?is)<script\b[^>]*>.*?</script>""")
        private val styleTagRegex = Regex("""(?is)<style\b[^>]*>.*?</style>""")
        private val htmlCommentRegex = Regex("""(?s)<!--.*?-->""")
        private val arabicLetterRegex = Regex("""[\u0600-\u06FF]""")
        private val suspiciousTokens = listOf(
            "window.",
            "function(",
            "gtag",
            "googletagmanager",
            "class=",
            "href=",
            "data-",
            "aria-label",
            "@media",
            "woff2"
        )

        private val tagRegex = Regex("<[^>]+>")
    }
}





