package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseCorrectAnswer(
        @JsonProperty("correctAnswerNum") val correctAnswerNum: Int,
        override val correctAnswerTotal: Int,
) : AbstractResponse(correctAnswerTotal)