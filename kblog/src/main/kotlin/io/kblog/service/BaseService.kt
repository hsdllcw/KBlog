package io.kblog.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

/**
 * The BaseService interface.
 * @author hsdllcw on 2020/4/3.
 * @version 1.0.0
 */
interface BaseService<T,E> {

    fun get(id: Int?): T?

    fun create(bean: T): T?

    fun createByVo(vo: E): T?

    fun update(bean: T): T?

    fun updateByVo(vo: E): T?

    fun findAll(): List<T>?

    fun findAll(spec: Specification<T>, pageable: Pageable): Page<T>?

    fun delete(id: Int)

    fun delete(bean: T)

    fun delete(beans: List<T>)

    fun deleteById(id: Int)

    fun deleteById(ids: List<Int>)
}