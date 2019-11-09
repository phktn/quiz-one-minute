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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;

@WebServlet(name = "StreamServlet", urlPatterns = { "/stream" })
public class StreamServlet extends HttpServlet {

    private final Object mLatchLockObject = new Object();

    private MyCountDownLatch mLatch = new MyCountDownLatch(1);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/octet-stream");
        try (PrintWriter out = resp.getWriter()) {
            MyCountDownLatch latch = getLatch();
            int id = FlagManager.getInstance().registerListener(this::countDown);
            printOutBotton(out, FlagManager.getInstance().getFlagId(), id);
            // noinspection InfiniteLoopStatement
            while (true) {
                latch.await();
                printOutBotton(out, latch.mFlagId, id);
                latch = getLatch();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printOutBotton(PrintWriter out, int flagId, int id) {
        if (flagId == -1) {
            out.println("<input onclick=\"send(" + id + ");\" type=\"button\" value=\"PUSH !\"/>");
        } else if (flagId == id) {
            out.println("<input disabled type=\"button\" value=\"Please answer !\" />");
        } else {
            out.println("<input disabled type=\"button\" value=\"Wait...\" />");
        }
        out.flush();
    }

    private void countDown(int flagId) {
        synchronized (mLatchLockObject) {
            mLatch.mFlagId = flagId;
            mLatch.countDown();
            mLatch = new MyCountDownLatch(1);

        }
    }

    private MyCountDownLatch getLatch() {
        synchronized (mLatchLockObject) {
            return mLatch;
        }
    }

    private static class MyCountDownLatch extends CountDownLatch {
        private int mFlagId = 0;

        private MyCountDownLatch(int count) {
            super(count);
        }
    }
}
