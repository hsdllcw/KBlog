package io.kblog.support.config

import org.springframework.context.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


/**
 * Jpa、Hibernate等的上下文配置
 */
@Configuration
@Import(OpooPressConfig::class,SecurityConfig::class)
@ComponentScan("io.kblog.service.impl")
class ContextConfig