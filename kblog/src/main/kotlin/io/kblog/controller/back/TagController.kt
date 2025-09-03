package io.kblog.controller.back

import io.kblog.controller.BaseController
import io.kblog.domain.Base
import io.kblog.domain.Tag
import io.kblog.service.TagService
import io.kblog.support.config.ContextConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The TagController class.
 * @author hsdllcw on 2020/4/18.
 * @version 1.0.0
 */
@RestController
@RequestMapping("${ContextConfig.ADMIN_API_URI}/tag")
class TagController : BaseController<Tag,Base.TagVo>() {
    @Autowired
    lateinit var tagService: TagService
}