package io.kblog.service.impl

import com.jeecms.common.jpa.SearchFilter
import io.kblog.domain.Page
import io.kblog.domain.User
import io.kblog.domain.Base
import io.kblog.domain.Category
import io.kblog.repository.CategoryDao
import io.kblog.repository.PageDao
import io.kblog.service.PageService
import io.kblog.service.TagService
import javassist.NotFoundException
import org.opoo.press.SiteManager
import org.opoo.press.impl.KSiteImpl
import org.opoo.util.PathUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.charset.Charset

/**
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */

@Service
@Transactional(readOnly = true)
class PageServiceImpl : PageService, BaseServiceImpl<Page, Base.PageVo>() {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Autowired
    lateinit var pageDao: PageDao

    @Autowired
    lateinit var categoryDao: CategoryDao

    @Autowired
    lateinit var tagService: TagService

    @Autowired
    final lateinit var site: KSiteImpl

    @Autowired
    lateinit var siteManager: SiteManager

    override fun findByPath(path: String): Page? {
        return pageDao.findByPath(path)
    }

    override fun get(id: Int?): Page? {
        return super.get(id)?.apply {
            content = File(site.basedir, path!!).run {
                if (exists()) readText(Charset.forName("UTF-8")) else String()
            }
        }
    }

    @Transactional
    override fun create(bean: Page): Page? {
        bean.apply {
            creator = SecurityContextHolder.getContext().authentication.principal as User
            author = bean.author ?: creator!!.username
            category = category ?: categoryDao.findBySign("root") ?: categoryDao.save(Category().apply {
                sign = "root"
                name = "根栏目"
                treeCode = "0000"
            })
        }
        siteManager.createNewFile(site, bean.layout, bean.title, null, "html", null, null, mapOf("published" to bean.isPublished())).apply {
            writeText(bean.content ?: "", Charset.forName("UTF-8"))
            bean.path = this.absolutePath.substring(site.basedir.absolutePath.length)
        }
        return super.create(bean)
    }

    @Transactional
    override fun createByVo(vo: Base.PageVo): Page? {
        return create(Page().also { page ->
            BeanUtils.copyProperties(vo, page).run {
                page.apply {
                    this.tags = vo.tagIds?.map { it.toString() }?.toTypedArray()?.run { if (this.any()) this else null }?.let { tagIds ->
                        tagService.findAll(SearchFilter.spec(mapOf("IN_id" to tagIds)), PageRequest.of(0, Int.MAX_VALUE))?.content?.toHashSet()
                    } ?: mutableSetOf()
                    this.category = vo.categoryId?.toString()?.let { categoryId ->
                        categoryDao.findAll(SearchFilter.spec(mapOf("EQ_id" to arrayOf(categoryId))), PageRequest.of(0, Int.MAX_VALUE)).firstOrNull()
                    }
                }
            }
        })
    }

    @Transactional
    override fun updateByVo(vo: Base.PageVo): Page? {
        return update((get(vo.id) ?: throw NotFoundException("Page ID ${vo.id} Not Found")).apply {
            BeanUtils.copyProperties(vo, this)
            this.tags = vo.tagIds?.map { it.toString() }?.toTypedArray()?.run { if (this.any()) this else null }?.let { tagIds ->
                tagService.findAll(SearchFilter.spec(mapOf("IN_id" to tagIds)), PageRequest.of(0, Int.MAX_VALUE))?.content?.toHashSet()
            } ?: mutableSetOf()
            this.category = vo.categoryId?.toString()?.let { categoryId ->
                categoryDao.findAll(SearchFilter.spec(mapOf("EQ_id" to arrayOf(categoryId))), PageRequest.of(0, Int.MAX_VALUE)).firstOrNull()
            } ?: this.category
            File(site.basedir, path!!).apply {
                parentFile.mkdir()
                logger.info("Page file: {}", this)
                writeText(content ?: String(), Charset.forName("UTF-8"))
            }
        })
    }


    @Transactional
    override fun deleteById(id: Int) {
        get(id)?.let { page ->
            delete(page)
        }
    }

    @Transactional
    override fun deleteById(ids: List<Int>) {
        ids.forEach { id ->
            get(id)?.let { page ->
                delete(page)
            }
        }
    }

    @Transactional
    override fun delete(bean: Page) {
        super.delete(bean).apply {
            File(site.basedir, bean.path!!).delete()
        }
    }

    @Transactional
    override fun delete(beans: List<Page>) {
        beans.forEach { bean ->
            delete(bean)
        }
    }
}