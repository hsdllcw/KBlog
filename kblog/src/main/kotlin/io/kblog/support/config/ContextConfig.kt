package io.kblog.support.config

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.ZipUtil
import io.kblog.support.config.orm.DataSourceConfig
import org.opoo.util.PathUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.system.ApplicationHome
import org.springframework.context.annotation.*
import org.springframework.core.io.ClassPathResource
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.util.WebUtils
import java.io.File
import java.io.FileNotFoundException


/**
 * Jpa、Hibernate等的上下文配置
 */
@Configuration
@Import(OpooPressConfig::class, SecurityConfig::class, DataSourceConfig::class)
@ComponentScan("io.kblog.service.impl")
class ContextConfig {
    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val APIURI: String = "/api"
        const val ADMINAPIURI: String = "${APIURI}/admin"
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Autowired
    lateinit var webApplicationConnect: WebApplicationContext

    @Bean
    fun basedir(): File {
        return (if (isRunByJar()) {
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
        } else {
            ClassPathResource("/").file.let { classPathDir ->
                PathUtils.appendBaseIfNotAbsolute(classPathDir, "META-INF").also { basedir ->
                    arrayOf(
                            PathUtils.appendBaseIfNotAbsolute(basedir, "resources"),
                            PathUtils.appendBaseIfNotAbsolute(classPathDir, "static")
                    ).forEach { file ->
                        FileUtil.del(file)
                    }
                }
            }
        }).apply {
            logger.info("" +
                    "\n=================================================================================" +
                    "\nKBlog directory path is {}" +
                    "\n================================================================================="
                    , this.absolutePath
            )
        }
    }

    @Bean
    fun isRunByJar(): Boolean {
        return try {
            webApplicationConnect.let {
                it.servletContext?.let { servletContext ->
                    WebUtils.getRealPath(servletContext, "/")
                }
            }
            false
        } catch (e: FileNotFoundException) {
            true
        }
    }
}