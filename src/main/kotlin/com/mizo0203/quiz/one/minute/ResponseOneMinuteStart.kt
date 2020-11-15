package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseOneMinuteStart(
        @JsonProperty("startOneMinute") val startOneMinute: Boolean = true,
)