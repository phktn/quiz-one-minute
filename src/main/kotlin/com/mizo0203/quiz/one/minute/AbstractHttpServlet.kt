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

import java.io.IOException
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractHttpServlet : HttpServlet() {

    @Throws(IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        req.characterEncoding = "UTF-8"
        req.reader.use { input -> onReadLine(input.readLine() ?: "") }
    }

    abstract fun onReadLine(line: String)

    @WebServlet(name = "NicknameSetServlet", urlPatterns = ["/setNickname"])
    class NicknameSetServlet : AbstractHttpServlet() {
        override fun onReadLine(line: String) {
            FlagManager.instance.setNickname(line.trim())
            LOG.info("NicknameSetServlet doPost")
        }
    }

    @WebServlet(name = "ProblemSetServlet", urlPatterns = ["/selectProblemSet"])
    class ProblemSetServlet : AbstractHttpServlet() {
        override fun onReadLine(line: String) {
            FlagManager.instance.selectProblemSet(line.toInt())
            LOG.info("SkipServlet doPost")
        }
    }

    @WebServlet(name = "OneMinuteStartServlet", urlPatterns = ["/startOneMinute"])
    class OneMinuteStartServlet : AbstractHttpServlet() {
        override fun onReadLine(line: String) {
            FlagManager.instance.startOneMinute()
            LOG.info("OneMinuteStartServlet doPost")
        }
    }

    @WebServlet(name = "CorrectAnswerSetServlet", urlPatterns = ["/setCorrectAnswer"])
    class CorrectAnswerSetServlet : AbstractHttpServlet() {
        override fun onReadLine(line: String) {
            FlagManager.instance.setCorrectAnswer(line.toInt())
            LOG.info("CorrectAnswerSetServlet doPost")
        }
    }

    companion object {
        private val LOG = Logger.getLogger(AbstractHttpServlet::class.java.name)
    }
}
