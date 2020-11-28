package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseMessage(
        @JsonProperty("problemSetNum") val problemSetNum: Int = -1,
        @JsonProperty("startOneMinute") val startOneMinute: Boolean = false,
        @JsonProperty("correctAnswerNum") val correctAnswerNum: Int = -1,
        @JsonProperty("correctAnswerTotal") val correctAnswerTotal: Int = -1,
)
