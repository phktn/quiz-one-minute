/*
 * Copyright 2019, Satoki Mizoguchi
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

package com.mizo0203.fastest.finger.first

import java.io.IOException
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "ChallengeServlet", urlPatterns = ["/challenge"])
class ChallengeServlet : HttpServlet() {

    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        req.characterEncoding = "UTF-8"
        val id = Integer.parseInt(req.getParameter("id"))
        val delayMs = FlagManager.instance.challenge(id)
        LOG.info("SendServlet doGet id: $id delayMs: $delayMs")
        if (delayMs != 0L) {
            resp.characterEncoding = "UTF-8"
            resp.writer.use { out -> out.print("惜しい！ ($delayMs ms 遅れ)") }
        }
    }

    companion object {
        private val LOG = Logger.getLogger(ChallengeServlet::class.java.name)
    }
}
