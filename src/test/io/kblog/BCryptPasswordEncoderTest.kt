package io.kblog

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * The BCryptPasswordEncoderTest class.
 * @author hsdllcw on 2020/4/8.
 * @version 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
@Transactional
class BCryptPasswordEncoderTest {
    @Autowired
    private val passwordEncoder: BCryptPasswordEncoder? = null

    @Test
    fun test(){
        println(passwordEncoder?.encode("123456"))
    }
}