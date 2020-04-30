package io.kblog.support.config.orm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.util.WebUtils
import java.io.FileNotFoundException
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * The JpaEntityManager class.
 * @author hsdllcw on 2020/4/29.
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(JpaProperties::class)
class JpaEntityManager {
    @Autowired
    lateinit var jpaProperties: JpaProperties

    @Autowired
    @Qualifier("mysqlDataSource")
    lateinit var mysqlDataSource: DataSource

    @Autowired
    @Qualifier("h2DataSource")
    lateinit var h2DataSource: DataSource

    @Autowired
    var isRunByJar: Boolean = true

    @Autowired
    lateinit var webApplicationConnect: WebApplicationContext

    val dataSource: DataSource
        get() {
            return try {
                webApplicationConnect.let {
                    it.servletContext?.let { servletContext ->
                        WebUtils.getRealPath(servletContext, "/")
                    }
                }
                mysqlDataSource
            } catch (e: FileNotFoundException) {
                h2DataSource
            }
        }

    @Bean
    fun entityManagerFactoryBean(builder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean {

        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.properties.apply {
                    this["hibernate.dialect"] = if (isRunByJar) "org.hibernate.dialect.H2Dialect" else "org.hibernate.dialect.MySQL57Dialect"
                })
                .packages("io.kblog.domain")
                .build()
    }

    @Bean
    @Primary
    fun entityManagerFactory(builder: EntityManagerFactoryBuilder): EntityManagerFactory {
        return this.entityManagerFactoryBean(builder).getObject()!!
    }
}