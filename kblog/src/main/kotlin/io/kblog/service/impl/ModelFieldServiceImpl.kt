package io.kblog.service.impl

import io.kblog.domain.Base
import io.kblog.domain.ModelField
import io.kblog.repository.ModelFieldDao
import io.kblog.service.ModelFieldService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class ModelFieldServiceImpl : ModelFieldService, BaseServiceImpl<ModelField, Base.ModelFieldVo>() {
    @Autowired
    var modelFieldDao: ModelFieldDao? = null
}