package io.kblog.service

import io.kblog.domain.Role
import io.kblog.domain.User


interface RoleService : BaseService<Role> {
    fun getAuthorityByUser(user: User): Set<String>
}