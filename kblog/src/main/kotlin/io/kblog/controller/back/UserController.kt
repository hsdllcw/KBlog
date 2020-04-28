package io.kblog.controller.back

import io.kblog.controller.BaseController
import io.kblog.domain.Base
import io.kblog.domain.User
import io.kblog.service.UserService
import io.kblog.support.common.ResponseBean
import io.kblog.support.config.ContextConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The UserController class.
 * @author hsdllcw on 2020/4/18.
 * @version 1.0.0
 */
@RestController
@RequestMapping("${ContextConfig.ADMINAPIURI}/user")
class UserController : BaseController<User,Base.UserVo>() {

    @Autowired
    lateinit var userService: UserService

    @RequestMapping(value = ["/{id}", "/current"])
    override fun get(@PathVariable(required = false) id: Int?): Any {
        return ResponseBean(if (id != null) userService.get(id)
        else SecurityContextHolder.getContext().authentication.principal)
    }
}