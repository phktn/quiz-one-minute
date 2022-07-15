/*
 * Copyright 2020, P Hackathon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

abstract class AbstractHttpServlet {

    @Throws(IOException::class)
    open fun doPost(req: HttpServletRequest): String {
        return req.reader.use { input -> onReadLine(input.readLine() ?: "") }
    }

    abstract fun onReadLine(line: String): String

    @RestController
    class ProblemSetServlet(
        private val flagManager: FlagManager,
        private val objectMapper: ObjectMapper,
    ) :
        AbstractHttpServlet() {
        @PostMapping(value = ["/selectProblemSet"])
        override fun doPost(req: HttpServletRequest) = super.doPost(req)

        override fun onReadLine(line: String): String {
            val message = flagManager.selectProblemSet(line.toInt())
            LOG.info("ProblemSetServlet doPost")
            return objectMapper.writeValueAsString(message)
        }
    }

    @RestController
    class OneMinuteStartServlet(private val flagManager: FlagManager) : AbstractHttpServlet() {
        @PostMapping(value = ["/startOneMinute"])
        override fun doPost(req: HttpServletRequest) = super.doPost(req)

        override fun onReadLine(line: String): String {
            flagManager.startOneMinute()
            LOG.info("OneMinuteStartServlet doPost")
            return ""
        }
    }

    @RestController
    class CorrectAnswerSetServlet(
        private val flagManager: FlagManager,
        private val objectMapper: ObjectMapper,
    ) : AbstractHttpServlet() {
        @PostMapping(value = ["/setCorrectAnswer"])
        override fun doPost(req: HttpServletRequest) = super.doPost(req)

        override fun onReadLine(line: String): String {
            val message = flagManager.setCorrectAnswer(line.toInt())
            LOG.info("CorrectAnswerSetServlet doPost")
            return objectMapper.writeValueAsString(message)
        }
    }

    companion object {
        private val LOG = Logger.getLogger(AbstractHttpServlet::class.java.name)
    }
}
