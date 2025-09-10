package io.kblog.support.aop

import com.jeecms.common.jpa.SearchFilter
import io.kblog.service.PageService
import org.apache.commons.lang3.Strings
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.opoo.press.impl.KSiteImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File

/**
 * The BuildAspect class.
 * @author hsdllcw on 2020/4/28.
 * @version 1.0.0
 */
@Aspect
@Component
class BuildAspect {
    @Autowired
    lateinit var site: KSiteImpl

    @Autowired
    lateinit var pageService: PageService

    @Pointcut("execution(* io.kblog.controller.BaseController.create(..)) || execution(* io.kblog.controller.BaseController.update(..)) || execution(* io.kblog.controller.BaseController.delete(..))")
    fun buildAspect(): Unit = Unit

    @AfterReturning(pointcut = "buildAspect()")
    @Transactional(readOnly = true)
    fun build(joinPoint: JoinPoint) {
        if (site.config.get<List<String>>("source_dirs")
                .filter { sourceDir -> File(site.basedir, sourceDir).listFiles()?.any() == true }.any()
        ) {
            // 构建atom.xml
            site.set(
                "pageList", pageService.findAll(
                    SearchFilter.spec(mutableMapOf<String, Array<String>>().apply { emptyMap<String, Array<String>>() }),
                    PageRequest.of(
                        0,
                        Int.MAX_VALUE,
                        Sort.by(
                            Sort.Direction.DESC,
                            "id"
                        )
                    )
                )
            )
            site.build(Strings.CS.equals(joinPoint.signature.name, "delete"))
        }
    }
}