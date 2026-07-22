package com.devlomi.tahaqqaqhadith.di

import com.devlomi.tahaqqaqhadith.DriverFactory
import com.devlomi.tahaqqaqhadith.data.cache.FakeHadithCache
import com.devlomi.tahaqqaqhadith.data.cache.FakeHadithCacheImpl
import com.devlomi.tahaqqaqhadith.datasource.cache.Database
import org.koin.dsl.module

fun cacheModule() = module {

    single<Database> {
        Database(get())//TODO HANDLE PLATFORM MODULE DRIVER
    }

    single<FakeHadithCache> {
        FakeHadithCacheImpl(get<Database>().fakeHadithQueries)
    }
}