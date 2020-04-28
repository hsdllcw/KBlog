package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.Model
import io.kblog.repository.ModelDao
import io.kblog.service.ModelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class ModelServiceImpl : ModelService, BaseServiceImpl<Model,Base.ModelVo>() {
    @Autowired
    var modelDao: ModelDao? = null
}