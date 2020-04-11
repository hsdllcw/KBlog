package io.kblog.service

import io.kblog.domain.User
import org.springframework.security.core.userdetails.UserDetailsService


interface UserService : BaseService<User>, UserDetailsService {
    fun findByUsername(username: String?): User?
}