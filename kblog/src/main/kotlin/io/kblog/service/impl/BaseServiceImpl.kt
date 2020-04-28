package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.repository.BaseDao
import io.kblog.service.BaseService
import io.kblog.support.common.ResponseBean
import javassist.NotFoundException
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * The BaseServiceImpl class.
 * @author hsdllcw on 2020/4/3.
 * @version 1.0.0
 */
@Transactional(readOnly = true)
abstract class BaseServiceImpl<T, E> : BaseService<T, E> {

    @Autowired
    lateinit var baseDao: BaseDao<T>

    override fun get(id: Int?): T? {
        return baseDao.findByIdOrNull(id)
    }

    private fun save(bean: T): T? {
        return baseDao.save(bean)
    }

    @Transactional
    override fun update(bean: T): T? {
        return save(bean)
    }

    @Transactional
    override fun updateByVo(@RequestBody vo: E): T? {
        return update((get((vo as Base).id) ?: throw NotFoundException("VO ID ${vo.id} Not Found")).let { bean ->
            BeanUtils.copyProperties(vo, bean as Any).run {
                bean
            }
        })
    }

    @Transactional
    override fun create(bean: T): T? {
        return save(bean)
    }

    override fun createByVo(vo: E): T? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<T>? {
        return baseDao.findAll()
    }

    override fun findAll(spec: Specification<T>, pageable: Pageable): Page<T>? {
        return baseDao.findAll(spec, pageable)
    }

    @Transactional
    override fun delete(bean: T) {
        baseDao.delete(bean)
    }

    @Transactional
    override fun deleteById(id: Int) {
        baseDao.deleteById(id)
    }

    @Transactional
    override fun delete(beans: List<T>) {
        baseDao.deleteInBatch(beans)
    }

    @Transactional
    override fun deleteById(ids: List<Int>) {
        baseDao.deleteByIdIn(ids)
    }

    @Transactional
    override fun delete(id: Int) {
        baseDao.deleteById(id)
    }
}