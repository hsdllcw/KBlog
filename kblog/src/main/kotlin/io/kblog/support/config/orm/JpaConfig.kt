package io.kblog.support.config.orm

import io.kblog.support.config.ContextConfig.Companion.isRunByJar
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * The JpaConfig class.
 * @author hsdllcw on 2020/4/29.
 * @version 1.0.0
 */
@EnableConfigurationProperties(JpaProperties::class)
class JpaConfig : BeanDefinitionRegistryPostProcessor {
    lateinit var registry: BeanDefinitionRegistry
    lateinit var beanFactory: ConfigurableListableBeanFactory

    @Bean("dataSource")
    fun dataSource(): DataSource {
        return if (isRunByJar) {
            registry.removeBeanDefinition("mysqlDataSource")
            beanFactory.getBean("h2DataSource", DataSource::class.java)
        } else {
            registry.removeBeanDefinition("h2DataSource")
            beanFactory.getBean("mysqlDataSource", DataSource::class.java)
        }
    }

    @Bean
    fun entityManagerFactoryBean(builder: EntityManagerFactoryBuilder, @Qualifier("dataSource") dataSource: DataSource, jpaProperties: JpaProperties): LocalContainerEntityManagerFactoryBean {
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
    fun entityManagerFactory(entityManagerFactoryBean:LocalContainerEntityManagerFactoryBean): EntityManagerFactory {
        return entityManagerFactoryBean.getObject()!!
    }


    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        this.registry = registry
    }
}