package com.devlomi.tahaqqaqhadith.di

import com.devlomi.tahaqqaqhadith.data.parser.FakeHadithPageParser
import com.devlomi.tahaqqaqhadith.data.parser.HadithHtmlParser
import com.devlomi.tahaqqaqhadith.usecase.FetchFakeHadiths
import com.devlomi.tahaqqaqhadith.usecase.GetFakeHadithFromCache
import com.devlomi.tahaqqaqhadith.usecase.SearchForHadith
import com.devlomi.tahaqqaqhadith.usecase.SetFakeHadithSeen
import org.koin.dsl.module

fun useCaseModule() = module {
    factory<SearchForHadith> {
        SearchForHadith(get())
    }
    factory<FetchFakeHadiths> {
        FetchFakeHadiths(get(),get())
    }
    factory<GetFakeHadithFromCache> {
        GetFakeHadithFromCache(get())
    }

    factory<SetFakeHadithSeen> {
        SetFakeHadithSeen(get())
    }
    factory<HadithHtmlParser> {
        HadithHtmlParser()
    }
    factory<FakeHadithPageParser> {
        FakeHadithPageParser()
    }
}