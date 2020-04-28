package io.kblog.service

import io.kblog.domain.Page
import io.kblog.domain.Base

interface PageService : BaseService<Page,Base.PageVo> {
    fun findByPath(path: String): Page?
}