package com.devlomi.tahaqqaqhadith.ui.home

import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadith_Entity
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.Queue
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult

data class HomeState(
    var isLoading: Boolean = false,
    var data: HadithSearchResult? = null,
    var fakeHadith: FakeHadith_Entity? = null,//TODO USE DATASTATE?
    var query: String = "",
    var submittedSearchQuery: String = "",
    var queryPlaceholder: String = "",
    val queue: Queue<GenericMessageInfo> = Queue(mutableListOf()), // messages to be displayed in ui
)