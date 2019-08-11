package io.github.droidkaigi.confsched2020.data.api

interface GoogleFormApi {
    suspend fun submitSessionFeedback(
        sessionId: String,
        sessionTitle: String,
        totalEvaluation: Int,
        relevancy: Int,
        asExpected: Int,
        difficulty: Int,
        knowledgeable: Int,
        comment: String
    ): String
}
