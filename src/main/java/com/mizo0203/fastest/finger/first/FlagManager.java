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
    private int mFlagId = 0;

    private FlagManager() {
    }

    static FlagManager getInstance() {
        return sInstance;
    }

    void challenge(int id) {
        int flagId;
        synchronized (mFlagIdLockObject) {
            if (mFlagId == -1) {
                mFlagId = id;
            }
            flagId = mFlagId;
        }
        mListenerMap.get(id).onFlagIdChanged(flagId);
    }

    void start() {
        int flagId;
        synchronized (mFlagIdLockObject) {
            mFlagId = -1;
            flagId = mFlagId;
        }
        for (Listener listener : mListenerMap.values()) {
            listener.onFlagIdChanged(flagId);
        }
    }

    int registerListener(Listener listener) {
        mListenerMap.put(++mCnt, listener);
        return mCnt;
    }

    int getFlagId() {
        synchronized (mFlagIdLockObject) {
            return mFlagId;
        }
    }

    interface Listener {
        void onFlagIdChanged(int id);
    }
}
