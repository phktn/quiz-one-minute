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
    private val mListenerMap = ConcurrentHashMap<Int, Listener>()
    private val params = Params()

    fun selectProblemSet(num: Int) {
        sendMessageEventToAll { onSelectProblemSet(num) }
    }

    fun startOneMinute() {
        params {
            correctAnswerNumSet.clear()
        }
        sendMessageEventToAll { onStartOneMinute() }
    }

    fun setCorrectAnswer(num: Int) {
        params {
            correctAnswerNumSet.add(num)
            sendMessageEventToAll { onSetCorrectAnswer(num, correctAnswerNumSet.size) }
        }
    }

    private fun sendMessageEventToAll(block: Listener.() -> Unit) {
        HashMap(mListenerMap).forEach { (id, listener) ->
            try {
                block(listener)
            } catch (e: IOException) {
                mListenerMap.remove(id)
            }
        }
    }

    fun registerListener(id: Int, listener: Listener) {
        mListenerMap[id] = listener
    }

    internal interface Listener {
        @Throws(IOException::class)
        fun onSelectProblemSet(num: Int)

        @Throws(IOException::class)
        fun onStartOneMinute()

        @Throws(IOException::class)
        fun onSetCorrectAnswer(num: Int, total: Int)
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
