/*
 * Copyright 2019, P Hackathon
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

import java.io.IOException
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "LogServlet", urlPatterns = ["/log"])
class LogServlet : HttpServlet() {

    @Throws(IOException::class)
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        LOG.info(req.reader.readLine())
    }

    companion object {
        private val LOG = Logger.getLogger(LogServlet::class.java.name)
    }
}
