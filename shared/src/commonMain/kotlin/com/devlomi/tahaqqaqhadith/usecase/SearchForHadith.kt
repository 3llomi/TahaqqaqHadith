package com.devlomi.tahaqqaqhadith.usecase

import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.UIComponentType
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult
import com.devlomi.tahaqqaqhadith.data.network.SearchService
import kotlinx.coroutines.flow.flow

class SearchForHadith(private val searchService: SearchService) {
    fun execute(query: String) = flow<DataState<HadithSearchResult>> {
        emit(DataState.loading())
        try {
            val result = searchService.search(query)
            val sortedResult = result.copy(
                entries = result.entries.sortedByDescending { it.assessment.score }
            )
            emit(DataState.data(data = sortedResult))
        } catch (e: Exception) {
            emit(
                DataState.error(
                    message = GenericMessageInfo(
                        id = "search_error",
                        title = "Search Error",
                        description = e.message ?: "Unknown error",
                        uiComponentType = UIComponentType.Toast
                    )
                )
            )
        }
    }
}