package io.kblog.support.config

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.ZipUtil
import io.kblog.domain.Global
import io.kblog.service.GlobalService
import org.opoo.press.SiteManager
import org.opoo.press.impl.ConfigImpl
import org.opoo.press.impl.SiteManagerImpl
import org.opoo.util.PathUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.system.ApplicationHome
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.util.WebUtils
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileNotFoundException
import javax.annotation.Resource


/**
 * OpooPress的配置
 */
@Configuration
@ComponentScan("org.opoo.press")
class OpooPressConfig {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Autowired
    var webApplicationConnect: WebApplicationContext? = null

    @Autowired
    private lateinit var env: Environment

    @Resource
    private lateinit var arguments: ApplicationArguments

    @Bean("config")
    @Transactional
    fun config(globalService: GlobalService): ConfigImpl {
        return webApplicationConnect?.let {
            it.servletContext?.let { servletContext ->
                ConfigImpl(
                        (try {
                            /**
                             * 系统可以直接读取资源文件
                             */
                            File(WebUtils.getRealPath(servletContext, "/")).run {
                                ClassPathResource("/").file.let { classPathDir ->
                                    PathUtils.appendBaseIfNotAbsolute(classPathDir, "META-INF").also { basedir ->
                                        arrayOf(
                                                PathUtils.appendBaseIfNotAbsolute(basedir, "resources"),
                                                PathUtils.appendBaseIfNotAbsolute(classPathDir, "static")
                                        ).forEach { file ->
                                            if (exists()) FileUtil.del(file)
                                        }
                                    }
                                }
                            }
                        } catch (e: FileNotFoundException) {
                            /**
                             * 通过直接运行jar包的方式启动系统，不可以直接读取资源文件，要先解压资源再运行
                             */
                            File("${System.getProperty("user.dir")}${File.separator}KBlog").apply {
                                mkdir()
                                File("${System.getProperty("java.io.tmpdir")}${File.separator}KBlog").let { tempDir ->
                                    tempDir.mkdir()
                                    ZipUtil.unzip(ApplicationHome().source, tempDir)
                                    FileUtil.copy(PathUtils.appendBaseIfNotAbsolute(tempDir, "WEB-INF/classes/META-INF/themes"), this, false)
                                    FileUtil.del(tempDir)
                                }
                            }
                        }).apply {
                            logger.info("" +
                                    "\n=================================================================================" +
                                    "\nKBlog dir path is ${this.absolutePath}" +
                                    "\n================================================================================="
                            )
                        },
                        (globalService.findAll()?.firstOrNull() ?: globalService.create(Global().apply {
                            this.customs = Yaml().load<Map<String, String>>(ClassPathResource("config.yml").inputStream)
                        }))?.customs?.toMutableMap()?.apply {
                            this["dest_dir"] = try {
                                File(WebUtils.getRealPath(servletContext, "/")).absolutePath
                            } catch (e: FileNotFoundException) {
                                File("${System.getProperty("user.dir")}${File.separator}KBlog${File.separator}target").apply { mkdir() }.absolutePath
                            }
                        }?.toMap()
                ).apply {
                    get<List<String>>("source_dirs").forEach { sourceDir ->
                        File(this.basedir, sourceDir).mkdirs()
                    }
                }
            }
        } ?: throw BeanCreationException("config", ConfigImpl::class.java.toString())
    }

    @Bean
    fun siteManager(): SiteManager {
        return SiteManagerImpl()
    }
}