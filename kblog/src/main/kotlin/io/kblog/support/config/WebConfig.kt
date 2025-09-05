package io.kblog.support.config

import com.ruoyi.file.service.LocalSysFileServiceImpl
import io.kblog.support.filter.IndexFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * The WebConfig class.
 * @author hsdllcw on 2020/5/1.
 * @version 1.0.0
 */
@Import(IndexFilter::class, LocalSysFileServiceImpl::class)
class WebConfig : WebMvcConfigurer {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Bean
    fun indexFilterRegistration(indexFilter: IndexFilter): FilterRegistrationBean<IndexFilter> {
        return FilterRegistrationBean<IndexFilter>().apply {
            order = 1
            filter = indexFilter
            addUrlPatterns("/*")
            this.setName("indexFilter")
        }
    }
}