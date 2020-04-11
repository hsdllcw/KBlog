package io.kblog.domain

import org.springframework.security.core.GrantedAuthority
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

/**
 * The Role class.
 * @author hsdllcw on 2020/4/6.
 * @version 1.0.0
 */
@Entity
@Table(name = "kblog_role")
class Role(
        @Id
        var id: Int? = null,
        var name: String? = null,
        var description: String? = null,
        var allAuthority: Boolean = false,
        @XmlTransient
        @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
        var users: MutableSet<User> = mutableSetOf()
) : GrantedAuthority {
    private var authority: String? = null

    fun setAuthority(authority: String) {
        this.authority = authority
    }

    override fun getAuthority(): String {
        return this.authority!!
    }

}