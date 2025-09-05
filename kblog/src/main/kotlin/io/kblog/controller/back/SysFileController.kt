package io.kblog.controller.back

import com.ruoyi.file.service.ISysFileService
import com.ruoyi.system.api.domain.SysFile
import io.kblog.support.common.ResponseBean
import org.dromara.common.core.utils.file.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * 文件请求处理
 */
@RestController
class SysFileController {
    private val log: Logger = LoggerFactory.getLogger(SysFileController::class.java)

    @Autowired
    lateinit var sysFileService: ISysFileService

    /**
     * 文件上传请求
     */
    @PostMapping("upload")
    fun upload(file: MultipartFile): ResponseBean {
        try {
            // 上传并返回访问地址
            return ResponseBean(SysFile().apply {
                name = FileUtils.getName(url)
                url = sysFileService.uploadFile(file)
            })
        } catch (e: Exception) {
            log.error("上传文件失败", e)
            return ResponseBean(null, e.message, HttpStatus.BAD_GATEWAY.value())
        }
    }
}