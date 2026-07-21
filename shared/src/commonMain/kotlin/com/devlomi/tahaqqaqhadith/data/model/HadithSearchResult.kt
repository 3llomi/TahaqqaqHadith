package com.devlomi.tahaqqaqhadith.data.model

data class HadithSearchResult(
    val query: String,
    val entries: List<HadithEntry>,
    val overallAssessment: LegitimacyAssessment,
    val moreUrl: String?
)

data class HadithEntry(
    val index: Int,
    val hadithText: String,
    val narrator: String,
    val scholar: String,
    val source: String,
    val pageOrNumber: String,
    val verdict: String,
    val assessment: LegitimacyAssessment
)

data class LegitimacyAssessment(
    val score: Int,
    val state: LegitimacyState,
    val reason: String
)

enum class LegitimacyState {
    AUTHENTIC,
    NEEDS_REVIEW,
    WEAK_OR_REJECTED
}

