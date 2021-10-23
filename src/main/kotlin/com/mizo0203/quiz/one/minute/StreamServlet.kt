package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.databind.ObjectMapper
import info.macias.sse.servlet3.ServletEventTarget
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class StreamServlet(
    private val objectMapper: ObjectMapper,
    private val flagManager: FlagManager,
) {

    private val syncedCounter = object {
        private var mCnt = 0
        fun up(): Int {
            synchronized(this) {
                return ++mCnt
            }
        }
    }

    @GetMapping(value = ["/stream"])
    fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        req.characterEncoding = "UTF-8"
        resp.characterEncoding = "UTF-8"
        val target = ServletEventTarget(req).ok().open()
        val id = syncedCounter.up()
        flagManager.registerListener(id) { message ->
            val jsonBytes = objectMapper.writeValueAsBytes(message)
            target.send("message", String(jsonBytes, StandardCharsets.ISO_8859_1))
        }
    }
}
