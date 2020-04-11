package io.kblog.service

import io.kblog.domain.Page

/**
 * The BaseService interface.
 * @author hsdllcw on 2020/4/3.
 * @version 1.0.0
 */
interface BaseService<T> {

    fun get(id: Int): T?

    fun save(bean: T): T?

    fun update(bean: T): T?

    fun findAll(): List<T>?

    fun delete(bean: T)
}