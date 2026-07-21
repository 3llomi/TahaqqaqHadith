package com.devlomi.tahaqqaqhadith.domain

import com.devlomi.tahaqqaqhadith.data.model.HadithEntry
import com.devlomi.tahaqqaqhadith.data.model.LegitimacyAssessment
import com.devlomi.tahaqqaqhadith.data.model.LegitimacyState

class LegitimacyScorer {

    fun scoreVerdict(verdict: String): LegitimacyAssessment {
        val normalized = verdict.trim()
        if (normalized.isEmpty()) {
            return LegitimacyAssessment(
                score = 45,
                state = LegitimacyState.NEEDS_REVIEW,
                reason = "No clear grading found"
            )
        }

        var score = 50
        val hits = mutableListOf<String>()

        positiveSignals.forEach { (keyword, delta) ->
            if (normalized.contains(keyword)) {
                score += delta
                hits += keyword
            }
        }

        negativeSignals.forEach { (keyword, delta) ->
            if (normalized.contains(keyword)) {
                score += delta
                hits += keyword
            }
        }

        val bounded = score.coerceIn(0, 100)
        return LegitimacyAssessment(
            score = bounded,
            state = scoreToState(bounded),
            reason = if (hits.isEmpty()) "Unclassified verdict" else "Matched: ${hits.joinToString()}"
        )
    }

    fun scoreOverall(entries: List<HadithEntry>): LegitimacyAssessment {
        if (entries.isEmpty()) {
            return LegitimacyAssessment(
                score = 0,
                state = LegitimacyState.WEAK_OR_REJECTED,
                reason = "No results returned"
            )
        }

        val average = entries.map { it.assessment.score }.average()
        val strongest = entries.maxOf { it.assessment.score }
        val blended = ((strongest * 0.6) + (average * 0.4)).toInt().coerceIn(0, 100)

        return LegitimacyAssessment(
            score = blended,
            state = scoreToState(blended),
            reason = "Blend of strongest report and average grading"
        )
    }

    private fun scoreToState(score: Int): LegitimacyState {
        return when {
            score >= 75 -> LegitimacyState.AUTHENTIC
            score >= 45 -> LegitimacyState.NEEDS_REVIEW
            else -> LegitimacyState.WEAK_OR_REJECTED
        }
    }

    companion object {
        private val positiveSignals = mapOf(
            "صحيح" to 35,
            "حسن" to 22,
            "ثابت" to 20,
            "إسناده صحيح" to 35,
            "إسناده حسن" to 22
        )

        private val negativeSignals = mapOf(
            "ضعيف" to -35,
            "منكر" to -60,
            "موضوع" to -85,
            "باطل" to -85,
            "لا يصح" to -70,
            "لا أصل" to -90,
            "مجهول" to -30,
            "ليس بشيء" to -55,
            "يضعف" to -25,
            "لا أعلم فيه حديثا صحيحا" to -80,
            "لا تخلو من مقال" to -20
        )
    }
}

