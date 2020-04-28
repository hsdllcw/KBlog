package io.kblog.controller.back

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * The IndexController class.
 * @author hsdllcw on 2020/4/25.
 * @version 1.0.0
 */
@Controller
class IndexController {
    @RequestMapping("/admin/")
    fun index(): String {
        return "forward:/admin/index.html"
    }

    @GetMapping("/login", "/admin")
    fun login(): String {
        return "redirect:/admin/"
    }
}