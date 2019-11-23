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

import com.mizo0203.fastest.finger.first.FlagManager.Params
import java.io.IOException
import java.io.PrintWriter
import java.util.concurrent.CountDownLatch
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "StreamServlet", urlPatterns = ["/stream"])
class StreamServlet : HttpServlet() {

    private val mLatchLockObject = Any()

    private var mLatch = MyCountDownLatch(1)

    private val syncedLatch: MyCountDownLatch
        get() {
            synchronized(mLatchLockObject) {
                return mLatch
            }
        }

    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        req.characterEncoding = "UTF-8"
        resp.characterEncoding = "UTF-8"
        resp.contentType = "application/octet-stream"
        val id = FlagManager.instance.registerListener(object : FlagManager.Listener {
            override fun onFlagIdChanged(params: Params) {
                countDown(params)
            }
        })
        try {
            resp.writer.use { out ->
                printOutButton(out, FlagManager.instance.params, id)
                try {
                    while (true) {
                        val latch = syncedLatch
                        latch.await()
                        latch.mParams?.let { printOutButton(out, it, id) }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        } finally {
            FlagManager.instance.unregisterListener(id)
        }
    }

    private fun printOutButton(out: PrintWriter, params: Params, id: Int) {
        val msg = params.getMessage(id)
        if (params.flagId == -1) {
            if (params.skipId != id) {
                out.println("<p>$msg</p><input class=\"button is-primary is-large is-fullwidth\" onclick=\"send($id);\" type=\"button\" value=\"PUSH !\"/>")
            } else {
                out.println("<p>$msg</p><input class=\"button is-primary is-large is-fullwidth\" disabled type=\"button\" value=\"お休み中...\" />")
            }
        } else if (params.flagId == id) {
            out.println("<p>$msg</p><input class=\"button is-primary is-large is-fullwidth\" disabled type=\"button\" value=\"Please answer !\" />")
        } else {
            out.println("<p>$msg</p><input class=\"button is-primary is-large is-fullwidth\" disabled type=\"button\" value=\"Wait...\" />")
        }
        out.flush()
    }

    private fun countDown(params: Params) {
        synchronized(mLatchLockObject) {
            mLatch.mParams = params
            mLatch.countDown()
            mLatch = MyCountDownLatch(1)
        }
    }

    private class MyCountDownLatch(count: Int) : CountDownLatch(count) {
        internal var mParams: Params? = null
    }
}
