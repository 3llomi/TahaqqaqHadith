package com.devlomi.tahaqqaqhadith.usecase

import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.UIComponentType
import com.devlomi.tahaqqaqhadith.data.cache.FakeHadithCache
import kotlinx.coroutines.flow.flow

class GetFakeHadithFromCache(
    private val fakeHadithCache: FakeHadithCache
) {


    fun execute() = flow {
        emit(DataState.loading())
        try {
            val hadith = fakeHadithCache.getRandomNotSeenHadith()
            emit(DataState.data(data = hadith))
        } catch (e: Exception) {
            emit(
                DataState.error(
                    message = GenericMessageInfo(
                        id = "search_error",//TODO HANDLE ERRORS
                        title = "Search Error",
                        description = e.message ?: "Unknown error",
                        uiComponentType = UIComponentType.Toast
                    )
                )
            )
        }
    }
}