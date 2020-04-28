package io.kblog.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.NoRepositoryBean

/**
 * The BaseDao interface.
 * @author hsdllcw on 2020/4/3.
 * @version 1.0.0
 */
@NoRepositoryBean
interface BaseDao<T> : JpaRepository<T, Int>, JpaSpecificationExecutor<T> {

    @Modifying
    fun deleteByIdIn(ids: List<Int>)
}