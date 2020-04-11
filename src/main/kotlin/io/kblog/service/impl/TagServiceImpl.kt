package io.kblog.service.impl

import io.kblog.domain.Tag
import io.kblog.repository.TagDao
import io.kblog.service.TagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class TagServiceImpl : TagService, BaseServiceImpl<Tag>() {
    @Autowired
    var tagDao: TagDao? = null
}