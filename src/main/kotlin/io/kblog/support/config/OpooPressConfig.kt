package io.kblog.support.config

import io.kblog.service.GlobalService
import org.opoo.press.impl.ConfigImpl
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.util.WebUtils
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream


/**
 * OpooPress的配置
 */
@Configuration
@ComponentScan("org.opoo.press")
class OpooPressConfig {
    @Autowired
    var webApplicationConnect: WebApplicationContext? = null

    @Bean("config")
    fun config(globalService: GlobalService): ConfigImpl {
        return webApplicationConnect?.let {
            it.servletContext?.let { servletContext ->
                ConfigImpl(File(WebUtils.getRealPath(servletContext, "/")), globalService.get(1)?.customs ?: Yaml().load<Map<String, Any>>(ClassPathResource("config.yml").inputStream))
            }
        } ?: throw BeanCreationException("config", ConfigImpl::class.java.toString())
    }
}