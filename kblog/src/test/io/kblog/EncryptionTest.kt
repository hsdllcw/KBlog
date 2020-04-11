package io.kblog

import org.jasypt.encryption.StringEncryptor
import org.junit.Test

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
@Transactional
class EncryptionTest {
    @Autowired
    var encryptor: StringEncryptor? = null

    @Test
    fun encry() {
        //加密用户名
        val username = encryptor!!.encrypt("root")
        println(username)
        //加密密码
        val password = encryptor!!.encrypt("123456")
        println(password)
    }
}