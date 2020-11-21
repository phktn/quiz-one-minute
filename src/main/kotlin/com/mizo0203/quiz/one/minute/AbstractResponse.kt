package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

abstract class AbstractResponse(
        @JsonProperty("correctAnswerTotal") open val correctAnswerTotal: Int = 0,
)