package io.kblog.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.hibernate.validator.constraints.Length
import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

/**
 * The Base class.
 * @author hsdllcw on 2020/4/21.
 * @version 1.0.0
 */
interface Base {
    var id: Int?

    open class PageVo(
            override var id: Int? = null,
            open var title: String? = null,
            open var content: String? = null, //模板正文
            open var tagIds: List<Int>? = null,
            open var categoryId: Int? = null
    ) : Base

    open class UserVo(
            override var id: Int? = null,
            open var nickName: String? = null,
            open var gender: User.UserGender? = null,
            open var type: User.UserType = User.UserType.MEMBER,
            open var status: User.UserStatus = User.UserStatus.NORMAL
    ) : Base

    open class RoleVo(
            override var id: Int? = null,
            open var name: String? = null,
            open var description: String? = null,
            open var allAuthority: Boolean = false
    ) : Base

    open class CategoryVo(
            open var treeCode: String? = null,
            open var parentId: Int? = null
    ) : Base, TagVo()

    open class TagVo(
            override var id: Int? = null,
            open var name: String? = null,
            open var sign: String? = null
    ) : Base

    open class CollocatedVo(
            open var path: String? = null,
            open var type: String? = null,
            open var enabled: Boolean = true,
            open var icon: String = "star-on"
    ) : Base, CategoryVo()

    open class SiteVo(
            override var id: Int? = null,
            open var name: String? = null,
            open var sign: String? = null,
            open var domain: String? = null,
            open var templateTheme: String? = null,
            open var enabled: Boolean = true
    ) : Base

    open class GlobalVo(
            override var id: Int? = null,
            open var protocol: String = "http",
            open var port: Int = 8080,
            open var version: String = "1.0.0-SNAPSHOT",
            open var customs: Map<String, String?> = mutableMapOf()
    ) : Base

    open class ModelVo(
            override var id: Int? = null,
            open var customs: Map<String, String> = mutableMapOf(),
            open var type: String? = null,
            open var name: String? = null,
            open var number: String? = null
    ) : Base

    open class ModelFieldVo(
            override var id: Int? = null,
            open var customs: Map<String, String> = HashMap(0),
            open var model: Model? = null,
            open var type: Int? = null,
            open var innerType: Int? = null,
            open var label: String? = null,
            open var name: String? = null,
            open var prompt: String? = null,
            open var defValue: String? = null,
            open var required: Boolean? = null,
            open var disabled: Boolean? = null
    ) : Base

    open class OperationLogVo(
            override var id: Int? = null,
            open var site: Site? = null,
            open var user: User? = null,
            open var name: String? = null,
            open var dataId: Int? = null,
            open var description: String? = null,
            open var text: String? = null,
            open var ip: String? = null,
            open var country: String? = null,
            open var area: String? = null,
            open var time: Date? = null,
            open var type: Int? = null
    ) : Base
}