package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.User
import io.kblog.repository.RoleDao
import io.kblog.repository.UserDao
import io.kblog.service.RoleService
import io.kblog.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class UserServiceImpl : UserService, BaseServiceImpl<User, Base.UserVo>() {

    @Autowired
    private val passwordEncoder: BCryptPasswordEncoder? = null

    @Autowired
    private val userDao: UserDao? = null

    @Autowired
    private val roleService: RoleService? = null

    @Transactional
    override fun create(bean: User): User? {
        return super.create(bean.apply { password = passwordEncoder?.encode(password)!! })
    }

    override fun findByUsername(username: String?): User? {
        return username?.let { userDao?.findByUsername(it) }
    }

    @Transactional
    override fun loadUserByUsername(username: String?) = findByUsername(username)?.apply { roleService?.getAuthorityByUser(this) }
            ?: (if (username == "admin") create(User().apply {
                this.username = username
                password = "123456"
            }) else throw UsernameNotFoundException("$username Not Found"))
}