package io.kblog.support.config

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.ZipUtil
import io.kblog.support.config.orm.DataSourceConfig
import org.apache.commons.lang3.StringUtils
import org.opoo.util.PathUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.system.ApplicationHome
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.FileNotFoundException

/**
 * Jpa、Hibernate等的上下文配置
 */
@Configuration
@Import(WebConfig::class, OpooPressConfig::class, SecurityConfig::class, DataSourceConfig::class)
@ComponentScan("io.kblog.service.impl")
class ContextConfig {
    companion object {
        const val API_URI: String = "/api"
        const val ADMIN_API_URI: String = "${API_URI}/admin"
        const val KBLOG_DIR_NAME = "KBlog"
        const val WEBAPP_PATH = "webapp"
        val logger: Logger = LoggerFactory.getLogger(ContextConfig::class.java)
        val classPathDir = try {
            ClassPathResource("/").file
        } catch (e: FileNotFoundException) {
            ApplicationHome().source
        }

        private fun isTomcatEnvironment(): Boolean {
            return System.getProperty("catalina.base") != null ||
                    System.getProperty("catalina.home") != null ||
                    System.getProperty("tomcat.home") != null ||
                    System.getenv("CATALINA_HOME") != null ||
                    System.getenv("TOMCAT_HOME") != null
        }

        private fun isIdeEnvironment(classPath: String): Boolean {
            return System.getenv("IDEA_INITIAL_DIRECTORY") != null ||
                    System.getenv("ECLIPSE_HOME") != null ||
                    classPath.contains("idea_rt.jar") ||
                    classPath.contains("eclipse") ||
                    // 检查调试端口（IDE通常启用调试）
                    System.getProperty("agentlib:jdwp") != null ||
                    System.getProperty("Xdebug") != null
        }

        val detectDeploymentEnvironment: DeploymentEnvironment by lazy {
            val protocol = ContextConfig::class.java.getResource("")?.protocol
            val classPath = System.getProperty("java.class.path", "")
            when (protocol) {
                "jar" -> DeploymentEnvironment.JAR
                "file" -> {
                    when {
                        // Tomcat 环境检测
                        isTomcatEnvironment() -> DeploymentEnvironment.WAR
                        // IDE 环境检测
                        isIdeEnvironment(classPath) -> DeploymentEnvironment.IDE
                        // 其他文件协议（可能是命令行启动）
                        else -> DeploymentEnvironment.STANDALONE
                    }
                }

                else -> DeploymentEnvironment.UNKNOWN
            }
        }
        val isRunByJar = detectDeploymentEnvironment == DeploymentEnvironment.JAR
        val isRunByWar = detectDeploymentEnvironment == DeploymentEnvironment.WAR
    }

    @Value("\${kblog.path:}")
    var kblogPath: String? = null //war包部署的时候该配置无意义

    @Bean("basedir")
    fun getKblogDir(): File = if (StringUtils.isNotEmpty(kblogPath) && !isRunByWar) File(kblogPath!!) else {
        if (isRunByJar)
            PathUtils.appendBaseIfNotAbsolute(File(System.getProperty("user.dir")), KBLOG_DIR_NAME)
        else
            File(classPathDir, "META-INF")
    }

    @Bean("initializationBean")
    fun initializationBean(@Qualifier("basedir") kblogDir: File): String {
        initializeApplication(kblogDir)
        return "initialized"
    }

    @Bean
    fun configurableServletWebServerFactory(
        @Qualifier("basedir") kblogDir: File,
        @Qualifier("initializationBean") initializationBean: String
    ): ConfigurableServletWebServerFactory {
        logger.info(initializationBean)
        return TomcatServletWebServerFactory().apply {
            if (isRunByJar) {
                documentRoot = PathUtils.appendBaseIfNotAbsolute(kblogDir, WEBAPP_PATH).apply { mkdir() }
            }
        }
    }

    private fun initializeApplication(kblogDir: File) {
        val baseDir: File?
        if (isRunByJar) {
            baseDir = PathUtils.appendBaseIfNotAbsolute(
                File(System.getProperty("java.io.tmpdir")),
                KBLOG_DIR_NAME
            ).apply {
                mkdir()
            }
            ZipUtil.unzip(ApplicationHome().source, baseDir)
        } else baseDir = classPathDir
        // Copy themes
        FileUtil.copy(
            File(
                baseDir,
                if (isRunByJar) "WEB-INF/classes/META-INF/themes" else "META-INF/themes"
            ),
            kblogDir,
            false
        )
        if (isRunByJar) FileUtil.del(baseDir)
    }

    enum class DeploymentEnvironment {
        JAR,      // Spring Boot JAR 部署
        WAR,      // Tomcat WAR 部署
        IDE,      // IDE 中运行（微服务）
        STANDALONE, // 命令行独立运行
        UNKNOWN   // 未知环境
    }
}