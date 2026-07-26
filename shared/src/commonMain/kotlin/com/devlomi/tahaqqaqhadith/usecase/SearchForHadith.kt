package com.devlomi.tahaqqaqhadith.usecase

import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.UIComponentType
import com.devlomi.tahaqqaqhadith.common.util.Util
import com.devlomi.tahaqqaqhadith.data.model.HadithGroup
import com.devlomi.tahaqqaqhadith.data.network.HadithService
import kotlinx.coroutines.flow.flow
import kotlin.collections.component1
import kotlin.collections.component2

class SearchForHadith(private val hadithService: HadithService) {
    fun execute(query: String) = flow {
        emit(DataState.loading())
        try {
            val result = hadithService.search(query)

            val groups =  result.entries
                .groupBy { Util.sanitizeHadithText(it.hadithText) }
                .mapNotNull { (key, narrations) ->
                    val sorted = narrations.sortedByDescending { it.assessment.score }
                    val best = sorted.firstOrNull() ?: return@mapNotNull null
                    HadithGroup(key = key, bestEntry = best, narrations = sorted)
                }
                .sortedByDescending { it.bestEntry.assessment.score }


            emit(DataState.data(data = groups))
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