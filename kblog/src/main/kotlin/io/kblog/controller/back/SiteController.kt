package io.kblog.controller.back

import io.kblog.controller.BaseController
import io.kblog.domain.Base
import io.kblog.domain.Collocated
import io.kblog.domain.Site
import io.kblog.service.CollocatedService
import io.kblog.service.SiteService
import io.kblog.support.common.ResponseBean
import io.kblog.support.config.ContextConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The SiteController class.
 * @author hsdllcw on 2020/4/18.
 * @version 1.0.0
 */
@RestController
@RequestMapping("${ContextConfig.ADMIN_API_URI}/site")
class SiteController : BaseController<Site, Base.SiteVo>() {
    @Autowired
    lateinit var siteService: SiteService

    @GetMapping("first")
    fun getFirst(): Any {
        return ResponseBean(siteService.findAll()?.firstOrNull())
    }
}