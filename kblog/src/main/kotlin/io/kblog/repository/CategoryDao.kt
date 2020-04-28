package io.kblog.repository

import io.kblog.domain.Category

interface CategoryDao : BaseDao<Category>{
    fun findBySign(sign: String): Category?
}