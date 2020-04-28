package io.kblog.service

import io.kblog.domain.Base
import io.kblog.domain.Role
import io.kblog.domain.User


interface RoleService : BaseService<Role,Base.RoleVo> {
    fun getAuthorityByUser(user: User): Set<String>
}