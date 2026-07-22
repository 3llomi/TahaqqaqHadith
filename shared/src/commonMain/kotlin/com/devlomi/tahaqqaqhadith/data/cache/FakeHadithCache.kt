package com.devlomi.tahaqqaqhadith.data.cache

import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadith_Entity

interface FakeHadithCache {
    fun insertOrIgnore(fakeHadith: FakeHadith_Entity)
    fun bulkInsertOrIgnore(fakeHadiths: List<FakeHadith_Entity>)
    fun getPageNumbers(): List<Long>
    fun setHadithSeen(id:Long,boolean: Boolean)
    fun getRandomNotSeenHadith(): FakeHadith_Entity?
}