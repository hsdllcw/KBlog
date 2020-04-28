package io.kblog

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.ZipUtil
import io.kblog.support.config.OpooPressConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.opoo.util.PathUtils
import org.springframework.boot.runApplication
import org.springframework.boot.system.ApplicationHome
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

//@RunWith(SpringJUnit4ClassRunner::class)
//@SpringBootTest(classes = [Application::class])
//@WebAppConfiguration
//@Transactional
//@Rollback(value = false)
class FileTest {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}