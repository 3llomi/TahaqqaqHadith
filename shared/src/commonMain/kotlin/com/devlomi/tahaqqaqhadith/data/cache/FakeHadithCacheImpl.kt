package com.devlomi.tahaqqaqhadith.data.cache

import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadithQueries
import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadith_Entity
import com.devlomi.tahaqqaqhadith.common.toDBLong

class FakeHadithCacheImpl(private val fakeHadithQueries: FakeHadithQueries) : FakeHadithCache {
    override fun insertOrIgnore(fakeHadith: FakeHadith_Entity) {
        fakeHadithQueries.insertOrIgnore(
            id = fakeHadith.id,
            text = fakeHadith.text,
            page = fakeHadith.page,
            grade = fakeHadith.grade,
            correctHadithUrl = fakeHadith.correctHadithUrl,
            timestamp = fakeHadith.timestamp,
            seen = fakeHadith.seen
        )
    }

    override fun getPageNumbers(): List<Long> {
        return fakeHadithQueries.pageNumbers().executeAsList()
    }

    override fun setHadithSeen(id:Long,boolean: Boolean) {
        fakeHadithQueries.setHadithSeen(seen = boolean.toDBLong(), id = id)
    }

    override fun getRandomNotSeenHadith(): FakeHadith_Entity? {
        return fakeHadithQueries.getRandomNotSeenHadith().executeAsOneOrNull()
    }

    override fun bulkInsertOrIgnore(fakeHadiths: List<FakeHadith_Entity>) {
        fakeHadithQueries.transaction(false){
            fakeHadiths.forEach {
                insertOrIgnore(it)
            }
        }
    }
}