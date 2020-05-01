package io.kblog.support.config

import io.kblog.support.filter.IndexFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * The WebConfig class.
 * @author hsdllcw on 2020/5/1.
 * @version 1.0.0
 */
@Configuration
class WebConfig : WebMvcConfigurer, BeanDefinitionRegistryPostProcessor {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Autowired
    var isRunByJar: Boolean = true

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        if(isRunByJar){
            registry.registerBeanDefinition("indexFilter", RootBeanDefinition(IndexFilter::class.java))
        }
    }

}