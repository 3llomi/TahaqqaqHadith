package com.devlomi.tahaqqaqhadith.di

import com.devlomi.tahaqqaqhadith.data.network.SearchService
import com.devlomi.tahaqqaqhadith.usecase.SearchForHadith
import org.koin.dsl.module

fun useCaseModule() = module {
    factory<SearchForHadith> {
        SearchForHadith(get())
    }
}