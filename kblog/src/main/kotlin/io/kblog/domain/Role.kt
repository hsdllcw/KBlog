package io.kblog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
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
        override var id: Int? = null,
        override var name: String? = null,
        override var description: String? = null,
        override var allAuthority: Boolean = false,
        @XmlTransient
        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
        var users: MutableSet<User> = mutableSetOf()
) : GrantedAuthority, Base.RoleVo() {
    private var authority: String? = null

    fun setAuthority(authority: String) {
        this.authority = authority
    }

    override fun getAuthority(): String {
        return this.authority!!
    }

}