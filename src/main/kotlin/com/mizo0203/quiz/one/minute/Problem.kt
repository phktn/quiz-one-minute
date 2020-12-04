package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty

data class Problem(
    @JsonProperty("question") val question: String,
    @JsonProperty("answer") val answer: String,

    ) {
    data class Set(
        @JsonProperty("num") val num: Int = -1,
        @JsonProperty("problems") val problems: ArrayList<Problem> = arrayListOf(),
    )
}
