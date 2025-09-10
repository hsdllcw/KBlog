package io.kblog.support.config

import io.kblog.domain.Global
import io.kblog.domain.Site
import io.kblog.service.GlobalService
import io.kblog.service.SiteService
import org.apache.commons.lang3.StringUtils
import org.opoo.press.SiteManager
import org.opoo.press.impl.ConfigImpl
import org.opoo.press.impl.SiteManagerImpl
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${kblog.domain}")
    lateinit var domain: String

    @Value("\${kblog.url}")
    lateinit var url: String

    @Value("\${kblog.protocol}")
    lateinit var protocol: String

    @Value("\${kblog.port}")
    var port: Int? = null

    @Value("\${server.servlet.context-path}")
    lateinit var rootPath: String

    @Autowired
    lateinit var webApplicationConnect: WebApplicationContext

    @Transactional
    @Bean("config")
    fun config(globalService: GlobalService, siteService: SiteService): ConfigImpl {
        return webApplicationConnect.servletContext?.let { servletContext ->
            ConfigImpl(kblogDir,
                (globalService.findAll()?.firstOrNull() ?: globalService.create(Global().also { global ->
                    global.port = port
                    global.protocol = protocol
                    global.customs =
                        Yaml().load<Map<String, String>>(ClassPathResource("config.yml").inputStream).toMutableMap()
                    if (!StringUtils.isAllEmpty(global.customs["root"], rootPath)) {
                        global.customs["root"] = global.customs["root"] ?: rootPath
                    }
                    if (StringUtils.isEmpty(global.customs["url"])) {
                        global.customs["url"] = url
                    }
                })?.also { global ->
                    siteService.create(Site().also { site ->
                        site.name = global.customs["title"]
                        site.domain = domain
                        site.global = global
                    })
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