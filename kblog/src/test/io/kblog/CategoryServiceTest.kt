package io.kblog

import io.kblog.domain.Category
import io.kblog.service.CategoryService
import io.kblog.service.TagService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
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
@Rollback(value = false)
class CategoryServiceTest {
    @Autowired
    lateinit var categoryService: CategoryService

    @Autowired
    var tagService: TagService? = null

    @Test
    fun saveTest() {
        val root = categoryService.create(Category().apply {
            this.name = "Root"
            this.sign = "root"
            this.treeCode = "0000"
        })
        val java = categoryService.create(Category().apply {
            this.parent = root
            this.name = "Java"
            this.sign = "java"
            this.treeCode = "0000-0000"
        })
        val springBoot = categoryService.create(Category().apply {
            this.parent = java
            this.name = "SpringBoot"
            this.sign = "spring_boot"
            this.treeCode = "0000-0000-0000"
        })
        println(springBoot)
    }

    @Test
    fun findAllTest() {
        categoryService.findAll()?.forEach { category ->
            println(category)
        }
    }

    @Test
    fun deleteAllTest() {
        categoryService.deleteById(listOf(2, 23))
    }
}