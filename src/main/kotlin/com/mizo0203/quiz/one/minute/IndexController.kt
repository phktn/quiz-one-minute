package com.mizo0203.quiz.one.minute

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class IndexController {

    @GetMapping(value = ["/"])
    fun doGetIndex(req: HttpServletRequest) = "index"

    @GetMapping(value = ["/admin.html"])
    fun doGetAdmin(req: HttpServletRequest) = "admin"
}
