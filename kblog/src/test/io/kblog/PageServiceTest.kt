package io.kblog

import io.kblog.domain.Page
import io.kblog.service.CategoryService
import io.kblog.service.PageService
import io.kblog.service.TagService
import io.kblog.service.UserService
import org.springframework.beans.BeanUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * The PageServiceTest class.
 * @author hsdllcw on 2020/3/29.
 * @version 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
@Transactional
class PageServiceTest {
    @Autowired
    var pageService: PageService? = null

    @Autowired
    var categoryService: CategoryService? = null

    @Autowired
    var tagService: TagService? = null

    @Autowired
    var userService: UserService? = null

    @Test
    fun testSave() {
        pageService?.let {
            it.create(Page().apply {
                this.author = "admin"
                this.comments = false
                this.creator = userService?.get(5)
                this.desription = "就是一测试"
                this.excerpt = "就是一测试"
                this.layout = "post"
                this.title = "来自数据库的文档标题"
                this.path = "/2020-03-11-index.markdown"
                this.keywords = "测试"
                this.uri = "/article"
                this.status = Page.PageStatus.NORMAL
            })
        }
    }

    @Test
    @Rollback(value = false)
    fun testUpdate() {
        pageService?.let {
            it.findByPath("/2020-03-11-index.markdown")?.apply {
                tagService?.get(2)?.let { tag ->
                    this.tags.add(tag)
                }
            }?.let { page ->
                it.update(page)
            }
        }
    }

    @Test
    fun testFindByPath() {
        pageService?.findByPath("/2020-03-11-index.markdown")?.let { page ->
            println(page.category)
        }
    }

    @Test
    fun test() {
        val page = Page()
        BeanUtils.copyProperties(
                mapOf(
                        "author" to "admin",
                        "comments" to false,
                        "keywords" to "测试",
                        "uri" to "/article",
                        "status" to Page.PageStatus.NORMAL,
                        "creator" to userService?.get(5),
                        "desription" to "就是一测试",
                        "excerpt" to "就是一测试",
                        "layout" to "post",
                        "title" to "来自数据库的文档标题",
                        "path" to "/2020-03-11-index.markdown"
                ),
                page
        )

        println(page)
    }
}