package com.devlomi.tahaqqaqhadith.usecase

import co.touchlab.kermit.Logger
import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.UIComponentType
import com.devlomi.tahaqqaqhadith.data.cache.FakeHadithCache
import com.devlomi.tahaqqaqhadith.data.model.toEntityList
import com.devlomi.tahaqqaqhadith.data.network.HadithService
import kotlinx.coroutines.flow.flow

class FetchFakeHadiths(
    private val hadithService: HadithService,
    private val fakeHadithCache: FakeHadithCache
) {
    companion object {
        private const val MAX_PAGE = 90
        private const val MAX_HADITH_LENGTH = 600
    }

    fun execute() = flow {
        emit(DataState.loading())
        val randomPage = getRandomPage()
        try {
            val result = hadithService.getFakeHadiths(randomPage)
            val filteredResult =
                result.copy(items = result.items.filter { it.text.length < MAX_HADITH_LENGTH })
            val entities = filteredResult.toEntityList()
            fakeHadithCache.bulkInsertOrIgnore(entities)
            emit(DataState.data(data = result))

        } catch (e: Exception) {
            Logger.e {
                "Error fetching fake hadiths for page $randomPage: ${e.message}"
            }
            emit(
                DataState.error(
                    message = GenericMessageInfo(
                        id = "search_error",//TODO HANDLE SHOW ERRORS IN UI BASED ON THESE INFO
                        title = "Search Error",
                        description = e.message ?: "Unknown error",
                        uiComponentType = UIComponentType.Toast
                    )
                )
            )
        }
    }

    private fun getRandomPage(): Int {
        val currentPages = fakeHadithCache.getPageNumbers()
        val notExistsPageRange = (1..MAX_PAGE).filter { it.toLong() !in currentPages }
        return notExistsPageRange.random()
    }

}