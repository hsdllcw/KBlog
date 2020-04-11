package io.kblog.repository

import io.kblog.domain.Page

interface PageDao : BaseDao<Page>{
    fun findByPath(path:String): Page?
}