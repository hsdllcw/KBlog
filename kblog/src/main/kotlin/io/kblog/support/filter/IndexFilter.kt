package io.kblog.support.filter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
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
    private lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    override fun doFilter(request: ServletRequest, response: ServletResponse?, filterChain: FilterChain?) {
        if (requestMappingHandlerMapping.getHandler(request as HttpServletRequest) == null && request.requestURI.last() == '/') {
            request.getRequestDispatcher("index.html").forward(request, response)
        } else {
            filterChain?.doFilter(request, response)
        }
    }
}