package io.kblog.repository

import io.kblog.domain.Page
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query


interface PageDao : BaseDao<Page> {
    fun findByPath(path: String): Page?

//    @Modifying
//    @Query("delete from Page bean where bean.id in (:ids) ")
//    fun delete(ids: List<Int>)
}