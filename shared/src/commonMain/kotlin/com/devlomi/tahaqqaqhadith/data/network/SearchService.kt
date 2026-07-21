package com.devlomi.tahaqqaqhadith.data.network

import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult

interface SearchService {
    suspend fun search(query: String): HadithSearchResult
}