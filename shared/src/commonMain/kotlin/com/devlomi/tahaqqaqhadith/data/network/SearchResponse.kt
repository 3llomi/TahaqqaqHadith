package com.devlomi.tahaqqaqhadith.data.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class SearchResponse(
	val ahadith: AhadithPayload
)

data class AhadithPayload(
	val result: String
)

fun parseSearchResponse(rawJson: String): SearchResponse {
	val root = Json.parseToJsonElement(rawJson).jsonObject
	val ahadithObject = root["ahadith"]?.jsonObject
		?: error("Invalid response: missing 'ahadith' object")
	val html = ahadithObject["result"]?.jsonPrimitive?.content
		?: error("Invalid response: missing 'ahadith.result'")

	return SearchResponse(ahadith = AhadithPayload(result = html))
}