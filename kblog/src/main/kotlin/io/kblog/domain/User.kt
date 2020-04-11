package io.kblog.domain

import org.hibernate.validator.constraints.Length
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_user")
class User(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_user", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        var nickName: String? = null,
        @Length(max = 1)
        @Enumerated(EnumType.STRING)
        var gender: UserGender? = null,
        @Temporal(TemporalType.TIMESTAMP)
        var birthDate: Date? = null,
        @Enumerated(EnumType.STRING)
        var type: UserType = UserType.MEMBER,
        @Enumerated(EnumType.STRING)
        var status: UserStatus = UserStatus.NORMAL,
        @XmlTransient
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinTable(name = "kblog_user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
        var roles: MutableSet<Role> = mutableSetOf()
) : UserDetails {

    private var username: String? = null
    private var password: String? = null
    private var credentialsNonExpired: Boolean = true

    enum class UserGender {
        F, M, C
    }

    enum class UserStatus {
        NORMAL, LOCKED, UNACTIVATED, EXPIRED
    }

    enum class UserType {
        MEMBER, ADMIN, ROOT
    }

    @Transient
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles
    }

    @Transient
    override fun isEnabled(): Boolean {
        return status != UserStatus.UNACTIVATED
    }

    fun setUsername(username: String) {
        this.username = username
    }

    override fun getUsername(): String {
        return this.username!!
    }

    fun isCredentialsNonExpired(credentialsNonExpired: Boolean) {
        this.credentialsNonExpired = credentialsNonExpired
    }

    override fun isCredentialsNonExpired(): Boolean {
        return this.credentialsNonExpired
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getPassword(): String {
        return this.password!!
    }

    @Transient
    override fun isAccountNonExpired(): Boolean {
        return status != UserStatus.EXPIRED
    }

    @Transient
    override fun isAccountNonLocked(): Boolean {
        return status != UserStatus.LOCKED
    }
}