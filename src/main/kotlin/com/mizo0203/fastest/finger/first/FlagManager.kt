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

import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class FlagManager private constructor() {
    private val mListenerMap = ConcurrentHashMap<Int, Listener>()
    private val mFlagIdLockObject = Any()
    private var mParams = Params(USER_ID_NONE, USER_ID_NONE, USER_ID_NONE, USER_ID_NONE, "")
    private var mFlagTimeMs = -1L
    private val respondentList: ArrayDeque<Pair<Int, String>> = ArrayDeque()

    val params: Params
        get() {
            synchronized(mFlagIdLockObject) {
                return mParams
            }
        }

    fun challenge(id: Int, nickname: String): Long {
        var delayMs = 0L
        val params = synchronized(mFlagIdLockObject) {
            if (mParams.flagId == USER_ID_ALL) {
                mParams = Params(id, mParams.mSkipId, mParams.mSkipCnt, USER_ID_ALL, "$nickname さんが回答中")
                mFlagTimeMs = System.currentTimeMillis()
                return@synchronized mParams
            } else {
                for (pair in respondentList) {
                    if (pair.first == id) {
                        return -1L
                    }
                }
                delayMs = System.currentTimeMillis() - mFlagTimeMs
                respondentList.add(Pair(id, nickname))
                return@synchronized null
            }
        }
        params?.let {
            for (listener in mListenerMap.values) {
                listener.onFlagIdChanged(it)
            }
        }

        return delayMs
    }

    fun advance(): Boolean {
        synchronized(mFlagIdLockObject) {
            if (respondentList.isEmpty()) {
                return false
            }
        }
        val params = synchronized(mFlagIdLockObject) {
            val pair = respondentList.pop()
            mParams = Params(pair.first, mParams.mSkipId, mParams.mSkipCnt, USER_ID_ALL, "${pair.second} さんが回答中 (繰り上げ)")
            mFlagTimeMs = System.currentTimeMillis()
            return@synchronized mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
        return true
    }

    fun start() {
        val params = synchronized(mFlagIdLockObject) {
            mParams = Params(USER_ID_ALL, mParams.mSkipId, mParams.mSkipCnt - 1, USER_ID_NONE, "")
            respondentList.clear()
            return@synchronized mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
    }

    fun skip(msg: String) {
        val params = synchronized(mFlagIdLockObject) {
            mParams = Params(mParams.flagId, mParams.flagId, 1, mParams.flagId, msg)
            return@synchronized mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
    }

    fun message(msg: String) {
        val params = synchronized(mFlagIdLockObject) {
            mParams = Params(mParams.flagId, mParams.mSkipId, mParams.mSkipCnt, mParams.flagId, msg)
            return@synchronized mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
    }

    fun registerListener(id: Int, listener: Listener) {
        mListenerMap[id] = listener
    }

    fun unregisterListener(id: Int) {
        mListenerMap.remove(id)
    }

    internal interface Listener {
        fun onFlagIdChanged(params: Params)
    }

    /**
     * @param mSkipId お休み中の ID
     */
    internal class Params(val flagId: Int, val mSkipId: Int, val mSkipCnt: Int, private val mMsgId: Int, private val mMsg: String) {

        /**
         * お休み中の ID
         */
        val skipId: Int
            get() = if (mSkipCnt > -1) mSkipId else USER_ID_NONE

        fun getMessage(id: Int): String {
            return if (mMsgId == id || mMsgId == USER_ID_ALL) mMsg else ""
        }
    }

    companion object {
        const val USER_ID_NONE = 0
        const val USER_ID_ALL = -1
        val instance = FlagManager()
    }
}
