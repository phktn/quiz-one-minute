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

package com.mizo0203.fastest.finger.first;

import java.util.HashMap;
import java.util.Map;

class FlagManager {

    private static final FlagManager sInstance = new FlagManager();
    private final Map<Integer, Listener> mListenerMap = new HashMap<>();
    private final Object mFlagIdLockObject = new Object();
    private int mCnt = 0;
    private Params mParams = new Params(0, 0, "");
    private long mFlagTimeMs = -1L;

    private FlagManager() {
    }

    static FlagManager getInstance() {
        return sInstance;
    }

    long challenge(int id) {
        Params params;
        long delayMs = 0L;
        synchronized (mFlagIdLockObject) {
            if (mParams.mFlagId == -1) {
                mParams = new Params(id, 0, "");
                mFlagTimeMs = System.currentTimeMillis();
            } else {
                delayMs = System.currentTimeMillis() - mFlagTimeMs;
            }
            params = mParams;
        }
        mListenerMap.get(id).onFlagIdChanged(params);
        return delayMs;
    }

    void start() {
        Params params;
        synchronized (mFlagIdLockObject) {
            mParams = new Params(-1, 0, "");
            params = mParams;
        }
        for (Listener listener : mListenerMap.values()) {
            listener.onFlagIdChanged(params);
        }
    }

    void message(String msg) {
        Params params;
        synchronized (mFlagIdLockObject) {
            mParams = new Params(mParams.mFlagId, mParams.mFlagId, msg);
            params = mParams;
        }
        for (Listener listener : mListenerMap.values()) {
            listener.onFlagIdChanged(params);
        }
    }

    int registerListener(Listener listener) {
        mListenerMap.put(++mCnt, listener);
        return mCnt;
    }

    Params getParams() {
        synchronized (mFlagIdLockObject) {
            return mParams;
        }
    }

    interface Listener {
        void onFlagIdChanged(Params params);
    }

    static class Params {
        private int mFlagId;
        private int mMsgId;
        private String mMsg;

        Params(int flagId, int msgId, String msg) {
            mFlagId = flagId;
            mMsgId = msgId;
            mMsg = msg;
        }

        int getFlagId() {
            return mFlagId;
        }

        String getMessage(int id) {
            return mMsgId == id ? mMsg : "";
        }
    }
}
