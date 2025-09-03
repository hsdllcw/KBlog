package io.kblog.controller.back

import io.kblog.controller.BaseController
import io.kblog.domain.Base
import io.kblog.domain.Category
import io.kblog.service.CategoryService
import io.kblog.support.common.ResponseBean
import io.kblog.support.config.ContextConfig
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * The CategoryController class.
 * @author hsdllcw on 2020/4/18.
 * @version 1.0.0
 */
@RestController
@RequestMapping("${ContextConfig.ADMIN_API_URI}/category")
class CategoryController : BaseController<Category, Base.CategoryVo>() {
    @Autowired
    lateinit var categoryService: CategoryService

    @GetMapping(value = ["/{id}"])
    override fun get(@PathVariable(required = false) id: Int?): Any {
        return ResponseBean(categoryService.get(id)?.let { category ->
            Base.CategoryVo().let { categoryVo ->
                BeanUtils.copyProperties(category, categoryVo).run {
                    categoryVo.apply {
                        parentId = category.parent?.id
                    }
                }
            }
        })
    }
}