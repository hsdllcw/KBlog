package io.kblog.service.impl

import io.kblog.domain.Base
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
class CollocatedServiceImpl : CollocatedService, BaseServiceImpl<Collocated,Base.CollocatedVo>() {
    @Autowired
    lateinit var collocatedDao: CollocatedDao

    override fun create(bean: Collocated): Collocated? {
        if (bean.parent != null) {
            bean.parent?.children?.add(bean)
        }
        return super.create(bean)
    }
}