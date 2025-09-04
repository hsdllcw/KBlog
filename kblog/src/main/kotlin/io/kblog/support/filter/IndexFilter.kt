package io.kblog.support.filter

import cn.hutool.core.io.FileUtil
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.WebUtils
import java.io.File
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


/**
 * The IndexFilter class.
 * @author hsdllcw on 2020/5/1.
 * @version 1.0.0
 */
@Configuration
class IndexFilter : Filter {
    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Autowired
    private lateinit var webApplicationConnect: WebApplicationContext

    @Autowired
    private lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    private val webappDir: File? by lazy { File(WebUtils.getRealPath(webApplicationConnect.servletContext!!, "/")) }
    private val indexHtmlFile by lazy { File(webappDir, "index.html") }

    override fun doFilter(request: ServletRequest, response: ServletResponse?, filterChain: FilterChain?) {
        if (requestMappingHandlerMapping.getHandler(request as HttpServletRequest) == null && request.requestURI.last() == '/') {
            if (!File(webappDir, "index.html").exists()) {
                // Copy index.html
                IOUtils.copy(
                    resourceLoader.getResource("classpath:webapp/index.html").inputStream,
                    indexHtmlFile.outputStream()
                )
            }
            request.getRequestDispatcher("index.html").forward(request, response)
        } else {
            filterChain?.doFilter(request, response)
        }
    }
}