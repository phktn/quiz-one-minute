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

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mizo0203.quiz.one.minute.FlagManager.Params
import info.macias.sse.servlet3.ServletEventTarget
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "StreamServlet", urlPatterns = ["/stream"], asyncSupported = true)
class StreamServlet : HttpServlet() {

    private val syncedCounter = object {
        private var mCnt = 0
        fun up(): Int {
            synchronized(this) {
                return ++mCnt
            }
        }
    }

    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        req.characterEncoding = "UTF-8"
        resp.characterEncoding = "UTF-8"
        val target = ServletEventTarget(req).ok().open()
        val id = syncedCounter.up()
        FlagManager.instance.registerListener(id, object : FlagManager.Listener {
            @Throws(IOException::class)
            override fun onFlagIdChanged(params: Params) {
                try {
                    printOutButton(target, params, id)
                } catch (e: IOException) {
                    FlagManager.instance.unregisterListener(id)
                }
            }

            @Throws(IOException::class)
            override fun onSelectProblemSet(num: Int) {
                try {
                    val jsonBytes = jacksonObjectMapper().writeValueAsBytes(ResponseProblemSet(num))
                    target.send("message", String(jsonBytes, StandardCharsets.ISO_8859_1))
                } catch (e: IOException) {
                    FlagManager.instance.unregisterListener(id)
                }
            }

            @Throws(IOException::class)
            override fun onSetCorrectAnswer(num: Int) {
                val jsonBytes = jacksonObjectMapper().writeValueAsBytes(ResponseCorrectAnswer(num))
                target.send("message", String(jsonBytes, StandardCharsets.ISO_8859_1))
            }
        })
        printOutButton(target, FlagManager.instance.params, id)
    }

    @Throws(IOException::class)
    private fun printOutButton(target: ServletEventTarget, params: Params, id: Int) {
        val msg = params.getMessage(id)
        val data = if (params.flagId == FlagManager.USER_ID_ALL) {
            if (params.skipId != id) {
                "{\"delayMs\":\"$msg\",\"hero\":\"hero is-dark\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" onclick=\\\"send($id);\\\" type=\\\"button\\\" value=\\\"PUSH !\\\"/>\"}"
            } else {
                "{\"delayMs\":\"$msg\",\"hero\":\"hero\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"お休み中...\\\" />\"}"
            }
        } else if (params.flagId == id) {
            "{\"delayMs\":\"$msg\",\"hero\":\"hero is-primary\",\"button\":\"<input class=\\\"button is-primary is-inverted is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"Please answer !\\\" />\"}"
        } else {
            if (params.flagId == FlagManager.USER_ID_NONE) {
                "{\"delayMs\":\"$msg\",\"hero\":\"hero is-dark\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"Wait...\\\" />\"}"
            } else {
                if (params.skipId != id) {
                    "{\"delayMs\":\"$msg\",\"hero\":\"hero is-dark\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" onclick=\\\"send($id);\\\" type=\\\"button\\\" value=\\\"PUSH !\\\"/>\"}"
                } else {
                    "{\"delayMs\":\"$msg\",\"hero\":\"hero\",\"button\":\"<input class=\\\"button is-primary is-large is-fullwidth\\\" disabled type=\\\"button\\\" value=\\\"お休み中...\\\" />\"}"
                }
            }
        }
        target.send("message", String(data.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
    }
}
