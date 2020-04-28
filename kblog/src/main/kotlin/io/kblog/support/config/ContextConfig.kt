package io.kblog.support.config

import org.springframework.context.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * Jpa、Hibernate等的上下文配置
 */
@Configuration
@Import(OpooPressConfig::class, SecurityConfig::class)
@ComponentScan("io.kblog.service.impl")
class ContextConfig {
    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val APIURI: String = "/api"
        const val ADMINAPIURI: String = "${APIURI}/admin"
    }
}