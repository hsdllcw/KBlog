package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.Global
import io.kblog.repository.GlobalDao
import io.kblog.service.GlobalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class GlobalServiceImpl : GlobalService, BaseServiceImpl<Global,Base.GlobalVo>() {
    @Autowired
    var globalDao: GlobalDao? = null
}