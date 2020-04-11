package io.kblog.service.impl

import io.kblog.domain.Collocated
import io.kblog.repository.CollocatedDao
import io.kblog.service.CollocatedService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class CollocatedServiceImpl : CollocatedService, BaseServiceImpl<Collocated>() {
    @Autowired
    var collocatedDao: CollocatedDao? = null
}