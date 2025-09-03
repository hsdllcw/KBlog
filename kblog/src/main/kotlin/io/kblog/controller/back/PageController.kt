package io.kblog.controller.back

import io.kblog.controller.BaseController
import io.kblog.domain.Page
import io.kblog.domain.Base
import io.kblog.support.common.ResponseBean
import io.kblog.support.config.ContextConfig
import org.opoo.press.impl.KSiteImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * The PageController class.
 * @author hsdllcw on 2020/4/10.
 * @version 1.0.0
 */
@RestController
@RequestMapping("${ContextConfig.ADMIN_API_URI}/page")
class PageController : BaseController<Page, Base.PageVo>() {
    @Autowired
    lateinit var site: KSiteImpl

    @RequestMapping("/build/all")
    fun build(): Any {
        site.build(true)
        return ResponseBean()
    }
}