package io.kblog.service

import io.kblog.domain.Page

interface PageService : PageManageService, BaseService<Page> {
    fun findByPath(path: String): Page?
}