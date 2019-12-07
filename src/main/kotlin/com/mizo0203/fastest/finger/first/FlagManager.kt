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

internal class FlagManager private constructor() {
    private val mListenerMap = HashMap<Int, Listener>()
    private val mFlagIdLockObject = Any()
    private var mCnt = 0
    private var mParams = Params(0, 0, 0, 0, "")
    private var mFlagTimeMs = -1L

    val params: Params
        get() {
            synchronized(mFlagIdLockObject) {
                return mParams
            }
        }

    fun challenge(id: Int, nickname: String): Long {
        var params: Params? = null
        var delayMs = 0L
        synchronized(mFlagIdLockObject) {
            if (mParams.flagId == -1) {
                mParams = Params(id, mParams.mSkipId, mParams.mSkipCnt, -1, "$nickname さんが回答中")
                params = mParams
                mFlagTimeMs = System.currentTimeMillis()
            } else {
                delayMs = System.currentTimeMillis() - mFlagTimeMs
            }
        }
        if (params != null) {
            mListenerMap[id]?.onFlagIdChanged(params!!)
        }
        return delayMs
    }

    fun start() {
        var params: Params
        synchronized(mFlagIdLockObject) {
            mParams = Params(-1, mParams.mSkipId, mParams.mSkipCnt - 1, 0, "")
            params = mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
    }

    fun skip(msg: String) {
        var params: Params
        synchronized(mFlagIdLockObject) {
            mParams = Params(mParams.flagId, mParams.flagId, 1, mParams.flagId, msg)
            params = mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
    }

    fun message(msg: String) {
        var params: Params
        synchronized(mFlagIdLockObject) {
            mParams = Params(mParams.flagId, mParams.mSkipId, mParams.mSkipCnt, mParams.flagId, msg)
            params = mParams
        }
        for (listener in mListenerMap.values) {
            listener.onFlagIdChanged(params)
        }
    }

    fun registerListener(listener: Listener): Int {
        mListenerMap[++mCnt] = listener
        return mCnt
    }

    fun unregisterListener(id: Int) {
        mListenerMap.remove(id)
    }

    internal interface Listener {
        fun onFlagIdChanged(params: Params)
    }

    internal class Params(val flagId: Int, val mSkipId: Int, val mSkipCnt: Int, private val mMsgId: Int, private val mMsg: String) {

        val skipId: Int
            get() = if (mSkipCnt > -1) mSkipId else 0

        fun getMessage(id: Int): String {
            return if (mMsgId == id || mMsgId == -1) mMsg else ""
        }
    }

    companion object {
        val instance = FlagManager()
    }
}
