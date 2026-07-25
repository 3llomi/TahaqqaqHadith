package com.devlomi.tahaqqaqhadith.usecase

import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.UIComponentType
import com.devlomi.tahaqqaqhadith.data.cache.FakeHadithCache
import kotlinx.coroutines.flow.flow

class SetFakeHadithSeen(
    private val fakeHadithCache: FakeHadithCache
) {


    fun execute(id: Long,seen: Boolean) = flow {
        emit(DataState.loading())
        try {
            fakeHadithCache.setHadithSeen(id, seen)
            emit(DataState.data(data = Unit))
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