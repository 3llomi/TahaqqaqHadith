package com.devlomi.tahaqqaqhadith.data.model

import com.devlomi.tahaqqahhadith.datasource.cache.FakeHadith_Entity
import kotlin.time.Clock

data class FakeHadithPageResult(
    val page: Int,
    val sourceUrl: String,
    val items: List<FakeHadith>
)

data class FakeHadith(
    val number: Int,
    val hadith: String,
    val text: String,
    val grade: String?,
    val sahihAlternativeUrl: String?,
    val hadithUrl: String?
)

fun FakeHadithPageResult.toEntityList(): List<FakeHadith_Entity> {
    return items.map { it.toEntity(page) }
}
fun FakeHadith.toEntity(page: Int): FakeHadith_Entity {
    return FakeHadith_Entity(
        id = number.toLong(),
        text = text,
        page = page.toLong(),
        timestamp = Clock.System.now().toEpochMilliseconds(),
        correctHadithUrl = sahihAlternativeUrl,
        grade = this.grade,
        seen = 0L
    )
}