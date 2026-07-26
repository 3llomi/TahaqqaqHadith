package com.devlomi.tahaqqaqhadith.data.network

import co.touchlab.kermit.Logger
import com.devlomi.tahaqqaqhadith.data.model.FakeHadithPageResult
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult
import com.devlomi.tahaqqaqhadith.data.parser.FakeHadithPageParser
import com.devlomi.tahaqqaqhadith.data.parser.HadithHtmlParser
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request

class HadithServiceImpl(
    private val httpClient: HttpClient,
    private val parser: HadithHtmlParser,
    private val fakeHadithParser: FakeHadithPageParser,
) : HadithService {

    override suspend fun search(query: String): HadithSearchResult {
        val rawJson = httpClient.get("dorar_api.json") {
            // Dorar uses 'skey' for hadith search terms.
            parameter("skey", query)
        }.bodyAsText()

        val response = parseSearchResponse(rawJson)
        return parser.parse(query = query, html = response.ahadith.result)
    }

    override suspend fun getFakeHadiths(page: Int): FakeHadithPageResult {
        val request = httpClient.get("fake-hadith") {
            parameter("page", page.toString())
        }
        Logger.d { "RequestURL ${request.request.url}" }
        val rawHtml = request.bodyAsText()
        Logger.d { "Fetching fake hadiths for page $page -" }
        val parsed = fakeHadithParser.parse(page = page, rawContent = rawHtml)
        if (parsed.isHighConfidence()) return parsed



        Logger.d("SearchServiceImpl direct parse is empty/low confidence, trying fallback mirror for page $page")
        Logger.d("Direct parse result: ${parsed.items.size} items, isHighConfidence: ${parsed.isHighConfidence()}")
        Logger.d("Direct RAW result: $rawHtml")
        // Fallback mirror helps in environments where direct site access is challenge-protected.
        val mirroredContent = httpClient.get("https://r.jina.ai/http://dorar.net/fake-hadith") {
            parameter("page", page.toString())
        }.bodyAsText()

        return fakeHadithParser.parse(page = page, rawContent = mirroredContent)
    }

    private fun FakeHadithPageResult.isHighConfidence(): Boolean {
        if (items.isEmpty()) {
            Logger.d { "Items is empty" }
            return false
        }

        val validLinks = items.count { it.hadithUrl?.contains("/fake-hadith/") == true }
        val validGrades = items.count { !it.grade.isNullOrBlank() }
        val validBodies = items.count { it.hadith.length >= 12 }

        Logger.d { "Valid Links ${validLinks} Valid Grades $validGrades validBodies $validBodies" }
        // A trustworthy parse should have mostly plausible hadith text and some page links/grades.
        return validBodies >= (items.size * 0.8) && (validLinks > 0 || validGrades > 0)
    }
}

