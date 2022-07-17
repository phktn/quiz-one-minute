package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import java.io.IOException
import java.io.InputStream

@JsonPropertyOrder(
    "question",
    "answer",
)
class Problem(
    @JsonProperty("question") val question: String? = null,
    @JsonProperty("answer") val answer: String? = null,
) {
    override fun toString() = "$question / $answer"

    companion object {
        @Throws(IOException::class)
        fun parseProblemListFile(src: InputStream): List<Problem> = mutableListOf<Problem>().apply {
            val csvMapper = CsvMapper()
            csvMapper.readerFor(Problem::class.java)
                .with(csvMapper.schemaFor(Problem::class.java))
                .readValues<Problem>(src)
                .run { while (hasNext()) add(next()) }
        }.toList()
    }
}
