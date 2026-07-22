package com.devlomi.tahaqqaqhadith.data.model

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

