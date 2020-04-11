package io.kblog.service.impl

import io.kblog.domain.Category
import io.kblog.repository.CategoryDao
import io.kblog.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class CategoryServiceImpl : CategoryService, BaseServiceImpl<Category>() {
    @Autowired
    val categoryDao: CategoryDao? = null
}