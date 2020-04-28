package io.kblog.domain

import cn.hutool.core.date.DatePattern
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.validator.constraints.Length
import org.springframework.format.annotation.DateTimeFormat
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
        @Enumerated(EnumType.STRING)
        var type: UserType = UserType.MEMBER,
        @Enumerated(EnumType.STRING)
        var status: UserStatus = UserStatus.NORMAL,
        @XmlTransient
        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinTable(name = "kblog_user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
        var roles: MutableSet<Role> = mutableSetOf()
) : UserDetails {

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    var birthDate: Date? = null

    private var username: String? = null

    @JsonIgnore
    private var password: String? = null

    @JsonIgnore
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
    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return status != UserStatus.EXPIRED
    }

    @Transient
    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return status != UserStatus.LOCKED
    }
}