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

import org.springframework.stereotype.Service

@Service
class FlagManager {
    private val params = Params()

    fun selectProblemSet(num: Int) = ResponseMessage(problemSet = Define.problemSetList[num])

    fun startOneMinute() {
        params {
            correctAnswerNumSet.clear()
        }
    }

    fun setCorrectAnswer(num: Int): ResponseMessage = params {
        correctAnswerNumSet.add(num)
        return@params ResponseMessage(correctAnswerNum = num, correctAnswerTotal = correctAnswerNumSet.size)
    }

    internal class Params {
        private val lockObject = Any()
        val correctAnswerNumSet: MutableSet<Int> = mutableSetOf()
        operator fun <R> invoke(block: Params.() -> R) = synchronized(lockObject) { block() }
    }
}
