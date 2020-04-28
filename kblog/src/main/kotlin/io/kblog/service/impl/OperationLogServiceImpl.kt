package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.OperationLog
import io.kblog.repository.OperationLogDao
import io.kblog.service.OperationLogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class OperationLogServiceImpl : OperationLogService, BaseServiceImpl<OperationLog, Base.OperationLogVo>() {
    @Autowired
    var operationLogDao: OperationLogDao? = null
}