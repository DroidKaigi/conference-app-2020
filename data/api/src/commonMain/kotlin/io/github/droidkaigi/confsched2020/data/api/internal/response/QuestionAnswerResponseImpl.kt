package io.github.droidkaigi.confsched2020.data.api.internal.response

import io.github.droidkaigi.confsched2020.data.api.response.QuestionAnswerResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class QuestionAnswerResponseImpl(
    override val answerValue: String
) : QuestionAnswerResponse
