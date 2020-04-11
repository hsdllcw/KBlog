package io.kblog.service.impl

import io.kblog.domain.Page
import io.kblog.repository.PageDao
import io.kblog.service.PageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
class PageServiceImpl : PageService, PageManageServiceImpl() {
    @Autowired
    var pageDao: PageDao? = null

    override fun findByPath(path:String): Page? {
        return pageDao?.findByPath(path)
    }

    override fun save(bean: Page): Page? {
        build()
        return pageDao?.save(bean)
    }

    override fun update(bean: Page): Page? {
        build()
        return pageDao?.save(bean)
    }
}