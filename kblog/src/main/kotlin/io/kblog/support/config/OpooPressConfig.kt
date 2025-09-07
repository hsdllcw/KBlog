package io.kblog.support.config

import io.kblog.domain.Global
import io.kblog.domain.Site
import io.kblog.service.GlobalService
import io.kblog.service.SiteService
import org.opoo.press.SiteManager
import org.opoo.press.impl.ConfigImpl
import org.opoo.press.impl.SiteManagerImpl
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.util.WebUtils
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileNotFoundException


/**
 * OpooPress的配置
 */
@Configuration
@ComponentScan("org.opoo.press")
class OpooPressConfig {
    @Autowired
    @Qualifier("basedir")
    lateinit var kblogDir: File

    @Autowired
    lateinit var webApplicationConnect: WebApplicationContext

    @Transactional
    @Bean("config")
    fun config(globalService: GlobalService, siteService: SiteService): ConfigImpl {
        return webApplicationConnect.servletContext?.let { servletContext ->
            ConfigImpl(kblogDir,
                (globalService.findAll()?.firstOrNull() ?: globalService.create(Global().also { global ->
                    global.customs = Yaml().load<Map<String, String>>(ClassPathResource("config.yml").inputStream)
                }).also { global ->
                    siteService.create(Site().apply { this.global = global })
                })?.customs?.toMutableMap()?.also { customs ->
                    customs["dest_dir"] = try {
                        File(WebUtils.getRealPath(servletContext, "/")).absolutePath
                    } catch (e: FileNotFoundException) {
                        File(kblogDir, ContextConfig.WEBAPP_PATH).apply { mkdir() }.absolutePath
                    }
                }?.toMap()
            ).apply {
                get<List<String>>("source_dirs").forEach { sourceDir ->
                    File(this.basedir, sourceDir).mkdirs()
                }
            }
        } ?: throw BeanCreationException("Error creating bean with name configImpl")
    }

    @Bean
    fun siteManager(): SiteManager {
        return SiteManagerImpl()
    }
}