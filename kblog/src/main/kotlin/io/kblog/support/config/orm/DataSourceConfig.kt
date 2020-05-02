package io.kblog.support.config.orm

import io.kblog.support.config.ContextConfig.Companion.basedir
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.io.File
import javax.sql.DataSource

/**
 * The DataSourceConfig class.
 * @author hsdllcw on 2020/4/29.
 * @version 1.0.0
 */
@Configuration
@Import(JpaConfig::class)
class DataSourceConfig {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    fun mysqlDataSource(): DataSource? {
        return DataSourceBuilder.create().build()
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.h2")
    fun h2DataSource(): DataSource? {
        return DataSourceBuilder.create().apply {
            url("jdbc:h2:${File(basedir, "kblog").absolutePath};MODE=MYSQL;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE")
        }.build()
    }
}