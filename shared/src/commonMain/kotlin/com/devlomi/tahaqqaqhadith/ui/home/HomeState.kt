package com.devlomi.tahaqqaqhadith.ui.home

import com.devlomi.tahaqqaqhadith.common.GenericMessageInfo
import com.devlomi.tahaqqaqhadith.common.Queue
import com.devlomi.tahaqqaqhadith.data.model.HadithSearchResult

data class HomeState(
    var isLoading: Boolean = false,
    var data: HadithSearchResult? = null,
    val queue: Queue<GenericMessageInfo> = Queue(mutableListOf()), // messages to be displayed in ui
)