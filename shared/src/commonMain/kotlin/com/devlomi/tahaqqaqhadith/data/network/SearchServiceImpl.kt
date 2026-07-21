    package com.devlomi.tahaqqaqhadith.data.network

import com.devlomi.tahaqqaqhadith.BASE_URL
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult
import com.devlomi.tahaqqaqhadith.data.parser.HadithHtmlParser
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

class SearchServiceImpl(
    private val httpClient: HttpClient,
    private val parser: HadithHtmlParser = HadithHtmlParser()
) : SearchService {

    override suspend fun search(query: String): HadithSearchResult {
        val rawJson = httpClient.get(BASE_URL) {
            // Dorar uses 'skey' for hadith search terms.
            parameter("skey", query)
        }.bodyAsText()

        val response = parseSearchResponse(rawJson)
        return parser.parse(query = query, html = response.ahadith.result)
    }
}

