package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseProblemSet(
        @JsonProperty("problemSetNum") val problemSetNum: Int,
) : AbstractResponse()