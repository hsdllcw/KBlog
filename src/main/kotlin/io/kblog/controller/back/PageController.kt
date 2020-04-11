package io.kblog.controller.back

import io.kblog.service.PageService
import io.kblog.support.common.ResponseBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * The PageController class.
 * @author hsdllcw on 2020/4/10.
 * @version 1.0.0
 */
@Controller
@RequestMapping("/admin/page")
class PageController {

    @Autowired
    var pageService: PageService? = null

    @RequestMapping("/build/all")
    @ResponseBody
    fun build():Any {
        pageService?.build(true)
        return ResponseBean()
    }
}