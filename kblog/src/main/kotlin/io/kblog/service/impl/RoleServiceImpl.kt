package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.Role
import io.kblog.domain.User
import io.kblog.repository.RoleDao
import io.kblog.service.RoleService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class RoleServiceImpl : RoleService, BaseServiceImpl<Role,Base.RoleVo>() {

    @Autowired
    private val roleDao: RoleDao? = null

    override fun getAuthorityByUser(user: User): Set<String> {
        val roles = user.roles
        val set: MutableSet<String> = mutableSetOf()
        if (user.type == User.UserType.ROOT) {
            // root用户，拥有所有权限，直接返回
            set.add("*")
            return set
        }
        for (role in roles) {
            if (role.allAuthority) {
                // 拥有所有权限，直接返回
                set.add("*")
                return set
            }
        }
        for (role in roles) {
            val authority = role.authority
            if (StringUtils.isNotBlank(authority)) {
                for (perm in StringUtils.split(authority, ',')) {
                    set.add(perm)
                }
            }
        }
        return set
    }
}