/*
 * Copyright 2019, P Hackathon
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
import java.io.PrintWriter
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
        resp.characterEncoding = "UTF-8"
        val id = Integer.parseInt(req.getParameter("id"))
        val nickname = req.getParameter("nickname")
        if (nickname.isEmpty()) {
            resp.writer.use { out -> out.print("{\"delayMs\":\"ニックネームが未入力です！\",\"hero\":\"hero is-danger\"}") }
            return
        }
        val params = FlagManager.instance.challenge(id, nickname)
        resp.writer.use { out -> printOutButton(out, params, id) }
        LOG.info("SendServlet doGet id: $id")
    }

    // FIXME: Jackson による JSON 変換
    @Throws(IOException::class)
    private fun printOutButton(out: PrintWriter, params: FlagManager.Params, id: Int) {
        val msg = params.getMessage(id)
        out.print(if (params.flagId == FlagManager.USER_ID_ALL) {
            if (params.skipId != id) {
                "{\"delayMs\":\"$msg\",\"hero\":\"hero\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" onclick=\\\"send($id);\\\" type=\\\"button\\\" value=\\\"PUSH !\\\"/>\"}"
            } else {
                "{\"delayMs\":\"$msg\",\"hero\":\"hero is-dark\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"お休み中...\\\" />\"}"
            }
        } else if (params.flagId == id) {
            "{\"delayMs\":\"$msg\",\"hero\":\"hero is-primary\",\"button\":\"<input class=\\\"button is-primary is-inverted is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"Please answer !\\\" />\"}"
        } else {
            if (params.flagId == FlagManager.USER_ID_NONE) {
                "{\"delayMs\":\"$msg\",\"hero\":\"hero\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"Wait...\\\" />\"}"
            } else {
                if (params.skipId != id) {
                    "{\"delayMs\":\"$msg\",\"hero\":\"hero\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" onclick=\\\"send($id);\\\" type=\\\"button\\\" value=\\\"PUSH !\\\"/>\"}"
                } else {
                    "{\"delayMs\":\"$msg\",\"hero\":\"hero is-dark\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"お休み中...\\\" />\"}"
                }
            }
        })
    }

    companion object {
        private val LOG = Logger.getLogger(ChallengeServlet::class.java.name)
    }
}
