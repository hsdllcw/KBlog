package io.kblog.service.impl

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
class UserServiceImpl : UserService, BaseServiceImpl<User>() {

    @Autowired
    private val passwordEncoder: BCryptPasswordEncoder? = null

    @Autowired
    private val userDao: UserDao? = null

    @Autowired
    private val roleService: RoleService? = null

    @Transactional
    override fun save(bean: User): User? {
        return userDao?.save(bean.apply { password = passwordEncoder?.encode(password)!! })
    }

    override fun findByUsername(username: String?): User? {
        return username?.let { userDao?.findByUsername(it) }
    }

    override fun loadUserByUsername(username: String?) = findByUsername(username)?.apply { roleService?.getAuthorityByUser(this) }
            ?: throw UsernameNotFoundException("$username Not Found")
}