package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.databind.ObjectMapper
import spark.Request
import spark.Response
import spark.Spark.*

object Main {
    private val objectMapper = ObjectMapper()
    private val flagManager = FlagManager()

    @JvmStatic
    fun main(args: Array<String>) {
        kotlin.runCatching {
            // configure application.
            staticFiles.location("/static")

            // Starts the webapp on localhost and the port defined by the PORT
            // environment variable when present, otherwise on 8080.
            val port = System.getenv().getOrDefault("PORT", "8080").toInt()
            port(port)
            post("/selectProblemSet") { req: Request, _: Response ->
                val message = flagManager.selectProblemSet(req.body().toInt())
                log.info("ProblemSetServlet doPost")
                return@post objectMapper.writeValueAsString(message)
            }
            post("/startOneMinute") { _: Request, _: Response ->
                flagManager.startOneMinute()
                log.info("OneMinuteStartServlet doPost")
            }
            post("/setCorrectAnswer") { req: Request, _: Response ->
                val message = flagManager.setCorrectAnswer(req.body().toInt())
                log.info("CorrectAnswerSetServlet doPost")
                return@post objectMapper.writeValueAsString(message)
            }
        }.onFailure {
            log.severe(it.message)
            it.printStackTrace()
        }
    }
}
