package com.devlomi.tahaqqaqhadith.data.network

import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult
import com.devlomi.tahaqqaqhadith.data.model.FakeHadithPageResult

interface HadithService {
    suspend fun search(query: String): HadithSearchResult
    suspend fun getFakeHadiths(page: Int): FakeHadithPageResult
}