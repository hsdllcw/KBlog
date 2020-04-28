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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The SiteController class.
 * @author hsdllcw on 2020/4/18.
 * @version 1.0.0
 */
@RestController
@RequestMapping("${ContextConfig.ADMINAPIURI}/site")
class SiteController : BaseController<Site,Base.SiteVo>() {
    @Autowired
    lateinit var siteService: SiteService
}