package io.kblog.domain

import cn.hutool.core.date.DatePattern
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Suppress("PLUGIN_WARNING")
@Entity
@Table(name = "kblog_page")
class Page(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_page", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        override var id: Int? = null,
        var title: String? = null,
        var layout: String = "post",
        var path: String? = null,//模板路径(正文)
        @Transient
        var content: String? = null,//模板正文
        var uri: String? = null,//编译后的路径
        var comments: Boolean = false,
        var header: Boolean = true,
        var footer: Boolean = true,
        @XmlTransient
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "creator_id", nullable = false)
        var creator: User? = null,
        var author: String? = null,
        var keywords: String? = null,
        var desription: String? = null,
        @Column(name = "status", nullable = false)
        @Enumerated(EnumType.STRING)
        var status: PageStatus = PageStatus.NORMAL,
        var excerpt: String? = null,//文章的摘要
        var sort: Int = 1,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_id", nullable = false)
        var category: Category? = null,
        @XmlTransient
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "kblog_page_tag", joinColumns = [JoinColumn(name = "page_id")], inverseJoinColumns = [JoinColumn(name = "tag_id")])
        var tags: MutableSet<Tag> = mutableSetOf()
) : Base {
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    var date: Date = Calendar.getInstance().time

    enum class PageStatus(private val value: String, private val description: String) {
        NORMAL("NORMAL", "正常"), DRAFT("DRAFT", "草稿");

        @JsonValue
        fun description(): String {
            return description
        }

        fun value(): String {
            return value
        }

        companion object {
            @JsonCreator
            fun create(value: String): PageStatus {
                return valueOf(value) ?: throw IllegalArgumentException("No element matches $value")
            }
        }
    }

    fun isPublished(published: Boolean) {
        status = if (published) PageStatus.NORMAL else PageStatus.DRAFT
    }

    @Transient
    fun isPublished(): Boolean {
        return status == PageStatus.NORMAL
    }
}