package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseMessage(
    @JsonProperty("problemSet") val problemSet: Problem.Set? = null,
    @JsonProperty("correctAnswerNum") val correctAnswerNum: Int = -1,
    @JsonProperty("correctAnswerTotal") val correctAnswerTotal: Int = -1,
)
