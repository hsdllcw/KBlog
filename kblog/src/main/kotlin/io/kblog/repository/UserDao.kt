package io.kblog.repository

import io.kblog.domain.User

interface UserDao : BaseDao<User> {
    fun findByUsername(username: String): User?
}