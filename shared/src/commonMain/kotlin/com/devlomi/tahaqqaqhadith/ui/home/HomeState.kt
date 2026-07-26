package com.devlomi.tahaqqaqhadith.ui.home

import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadith_Entity
import com.devlomi.tahaqqaqhadith.common.DataState
import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.Queue
import com.devlomi.tahaqqaqhadith.data.model.HadithGroup
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult

data class HomeState(
    var searchResult: DataState<List<HadithGroup>>? = null,
    var fakeHadith: DataState<FakeHadith_Entity>? = null,
    var query: String = "",
    var submittedSearchQuery: String = "",
    var queryPlaceholder: String = "",
    val queue: Queue<GenericMessageInfo> = Queue(mutableListOf()), // messages to be displayed in ui
)