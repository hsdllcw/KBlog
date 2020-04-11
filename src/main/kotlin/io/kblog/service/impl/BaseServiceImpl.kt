package io.kblog.service.impl

import io.kblog.repository.BaseDao
import io.kblog.service.BaseService
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.AnnotationUtils.getAnnotation
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * The BaseServiceImpl class.
 * @author hsdllcw on 2020/4/3.
 * @version 1.0.0
 */
@Service
@Transactional(readOnly = true)
abstract class BaseServiceImpl<T> : BaseService<T> {

    @Autowired
    val baseDao: BaseDao<T>? = null

    override fun get(id: Int): T? {
        return baseDao?.findByIdOrNull(id)
    }

    @Transactional
    override fun save(bean: T): T? {
        return baseDao?.save(bean)
    }

    @Transactional
    override fun update(bean: T): T? {
        return baseDao?.save(bean)
    }

    override fun findAll(): List<T>? {
        return baseDao?.findAll()
    }

    @Transactional
    override fun delete(bean: T) {
        baseDao?.delete(bean)
    }
}