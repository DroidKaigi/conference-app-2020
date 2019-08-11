package io.github.droidkaigi.confsched2020.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class QuestionAnswerResponseImpl(
    override val answerValue: String
) : QuestionAnswerResponse
