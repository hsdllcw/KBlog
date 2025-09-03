package io.kblog.support.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import javax.annotation.Resource
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.declaredMembers


/**
 * The SecurityConfig class.
 * @author hsdllcw on 2020/4/8.
 * @version 1.0.0
 */
@EnableWebSecurity
@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userService: UserDetailsService

    @Autowired
    private lateinit var env: Environment

    @Resource
    private lateinit var arguments: ApplicationArguments

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .mvcMatchers("${ContextConfig.ADMIN_API_URI}/**")
                .authenticated()
                .and()
                .formLogin().apply {
                    arrayListOf("loginPage", "loginProcessingUrl").forEach { name ->
                        env.getProperty(name)?.let { value ->
                            this.javaClass.methods.firstOrNull { method -> method.name == name }?.invoke(this, value)
                        }
                    }
                }
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .httpBasic()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)?.passwordEncoder(passwordEncoder())
    }

}