package io.kblog.service.impl

import io.kblog.service.PageManageService
import org.opoo.press.impl.KSiteImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PageManageServiceImpl : PageManageService, KSiteImpl()