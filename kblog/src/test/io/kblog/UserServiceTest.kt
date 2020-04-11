package io.kblog

import io.kblog.service.UserService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * The UserServiceTest class.
 * @author hsdllcw on 2020/3/24.
 * @version 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [Application::class],webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {

    @Autowired
    var userService: UserService? = null

    @Test
    fun test() {
        userService?.findAll()?.forEach { println(it) }
    }
}