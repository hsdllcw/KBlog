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
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * The JpaConfig class.
 * @author hsdllcw on 2020/4/29.
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(JpaProperties::class)
class JpaConfig {
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
            return if (isRunByJar) {
                h2DataSource
            } else {
                mysqlDataSource
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