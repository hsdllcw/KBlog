package io.kblog

import io.kblog.support.config.ContextConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Import


@SpringBootApplication
@Import(ContextConfig::class)
class Application : SpringBootServletInitializer() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args).environment
        }
    }

    /**
     * war方式启动的处理方法
     */
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        /**
         * 如果应用在Java web服务器中部署，则使用自定义的登录页
         */
        return application.properties(mapOf("loginPage" to "/admin/", "loginProcessingUrl" to "/login", "runType" to "war")).sources(this::class.java)
    }
}