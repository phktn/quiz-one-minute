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
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class FlagManager private constructor() {
    private val mListenerMap = ConcurrentHashMap<Int, (ResponseMessage) -> Unit>()
    private val params = Params()

    fun setNickname(nickname: String) {
        sendMessageEventToAll(
                ResponseMessage(nickname = nickname)
        )
    }

    fun selectProblemSet(num: Int) {
        sendMessageEventToAll(ResponseMessage(problemSet = Define.problemSetList[num - 1]))
    }

    fun startOneMinute() {
        params {
            correctAnswerNumSet.clear()
        }
        sendMessageEventToAll(
                ResponseMessage(startOneMinute = true, correctAnswerTotal = 0)
        )
    }

    fun setCorrectAnswer(num: Int) {
        params {
            correctAnswerNumSet.add(num)
            sendMessageEventToAll(
                    ResponseMessage(correctAnswerNum = num, correctAnswerTotal = correctAnswerNumSet.size)
            )
        }
    }

    private fun sendMessageEventToAll(message: ResponseMessage) {
        HashMap(mListenerMap).forEach { (id, listener) ->
            try {
                listener.invoke(message)
            } catch (e: IOException) {
                mListenerMap.remove(id)
            }
        }
    }

    fun registerListener(id: Int, listener: (ResponseMessage) -> Unit) {
        mListenerMap[id] = listener
    }

    internal class Params {
        private val lockObject = Any()
        val correctAnswerNumSet: MutableSet<Int> = mutableSetOf()
        operator fun invoke(block: Params.() -> Unit) = synchronized(lockObject) { block() }
    }

    companion object {
        val instance = FlagManager()
    }
}
