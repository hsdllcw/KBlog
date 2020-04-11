package io.kblog

import io.kblog.domain.Category
import io.kblog.service.CategoryService
import io.kblog.service.TagService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * The CategoryServiceTest class.
 * @author hsdllcw on 2020/4/3.
 * @version 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
@Transactional
class CategoryServiceTest {
    @Autowired
    var categoryService: CategoryService? = null

    @Autowired
    var tagService: TagService? = null

    @Test
    fun saveTest() {
        println(categoryService?.save(Category().apply {
            this.name = "java"
            this.sign = "java"
            this.treeCode = "0000"
        }))
    }

    @Test
    fun findAllTest() {
//        categoryService?.findAll()?.forEach { category ->
//            println(category)
//        }
    }
}