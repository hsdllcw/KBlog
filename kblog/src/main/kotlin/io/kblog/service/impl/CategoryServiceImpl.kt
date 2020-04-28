package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.Category
import io.kblog.repository.CategoryDao
import io.kblog.service.CategoryService
import io.kblog.service.PageService
import javassist.NotFoundException
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class CategoryServiceImpl : CategoryService, BaseServiceImpl<Category, Base.CategoryVo>() {
    @Autowired
    lateinit var categoryDao: CategoryDao

    @Autowired
    lateinit var pageService: PageService

    override fun create(bean: Category): Category? {
        if (bean.parent != null) {
            bean.parent?.children?.add(bean)
        } else if (bean.sign != "root") {
            bean.parent = categoryDao.findBySign("root") ?: categoryDao.save(Category().apply {
                sign = "root"
                name = "根栏目"
                treeCode = "0000"
            })
        }
        return super.create(bean)
    }

    @Transactional
    override fun updateByVo(vo: Base.CategoryVo): Category? {
        return update((get(vo.id) ?: throw NotFoundException("VO ID ${vo.id} Not Found")).let { bean ->
            BeanUtils.copyProperties(vo, bean as Any).run {
                bean.apply {
                    parent = get(vo.parentId)
                }
            }
        })
    }

    @Transactional
    override fun createByVo(vo: Base.CategoryVo): Category? {
        return create(Category().also { category ->
            BeanUtils.copyProperties(vo, category).run {
                category.apply {
                    parent = get(vo.parentId)
                }
            }
        })
    }

    @Transactional
    override fun delete(bean: Category) {
        pageService.delete(bean.pages.toList())
        super.delete(bean)
    }

    @Transactional
    override fun deleteById(ids: List<Int>) {
        ids.forEach { id ->
            get(id)?.let { category ->
                delete(category)
            }
        }
    }

    @Transactional
    override fun deleteById(id: Int) {
        get(id)?.let { category ->
            delete(category)
        }
    }
}