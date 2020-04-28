package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.Site
import io.kblog.repository.SiteDao
import io.kblog.service.GlobalService
import io.kblog.service.SiteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class SiteServiceImpl : SiteService, BaseServiceImpl<Site, Base.SiteVo>() {
    @Autowired
    lateinit var siteDao: SiteDao
    @Autowired
    lateinit var globalService: GlobalService

    override fun create(bean: Site): Site? {
        bean.global = globalService.findAll()?.firstOrNull()
        return super.create(bean)
    }
}