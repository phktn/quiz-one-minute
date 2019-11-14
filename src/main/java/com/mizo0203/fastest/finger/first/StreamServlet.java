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
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/octet-stream");
        try (PrintWriter out = resp.getWriter()) {
            MyCountDownLatch latch = getLatch();
            int id = FlagManager.getInstance().registerListener(this::countDown);
            printOutButton(out, FlagManager.getInstance().getParams(), id);
            // noinspection InfiniteLoopStatement
            while (true) {
                latch.await();
                printOutButton(out, latch.mParams, id);
                latch = getLatch();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printOutButton(PrintWriter out, FlagManager.Params params, int id) {
        String msg = params.getMessage(id);
        if (params.getFlagId() == -1) {
            if (params.getSkipId() != id) {
                out.println("<input onclick=\"send(" + id + ");\" type=\"button\" value=\"PUSH !\"/>" + msg);
            } else {
                out.println("<input disabled type=\"button\" value=\"お休み中...\" />" + msg);
            }
        } else if (params.getFlagId() == id) {
            out.println("<input disabled type=\"button\" value=\"Please answer !\" />" + msg);
        } else {
            out.println("<input disabled type=\"button\" value=\"Wait...\" />" + msg);
        }
        out.flush();
    }

    private void countDown(FlagManager.Params params) {
        synchronized (mLatchLockObject) {
            mLatch.mParams = params;
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
        private FlagManager.Params mParams = null;

        private MyCountDownLatch(int count) {
            super(count);
        }
    }
}
