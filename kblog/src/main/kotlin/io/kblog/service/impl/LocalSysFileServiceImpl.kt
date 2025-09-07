package io.kblog.service.impl

import com.ruoyi.file.service.ISysFileService
import com.ruoyi.file.utils.FileUploadUtils
import io.kblog.service.GlobalService
import io.kblog.service.SiteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Primary
@Service
class LocalSysFileServiceImpl : ISysFileService {
    @Autowired
    lateinit var globalService: GlobalService

    @Autowired
    lateinit var siteService: SiteService

    /**
     * 资源映射路径 前缀
     */
    @Value("\${file.prefix:assets}")
    lateinit var localFilePrefix: String

    @Autowired
    @Qualifier("basedir")
    lateinit var kblogDir: File

    /**
     * 上传文件存储在本地的根路径
     */
    val localFilePath: String by lazy { File(kblogDir.absolutePath, localFilePrefix).absolutePath }

    /**
     * 域名或本机访问地址
     */
    val domain: String?
        get() {
            return globalService.get(1)?.let { global ->
                global.protocol + siteService.get(1)?.domain + (if (global.port == 443 || global.port == 80) ":$global.port" else "")
            }
        }

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    override fun uploadFile(file: MultipartFile?): String {
        return FileUploadUtils.upload(localFilePath, file).let { name -> domain + localFilePrefix + name }
    }
}