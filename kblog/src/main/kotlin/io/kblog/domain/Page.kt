package io.kblog.domain

import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient
import kotlin.collections.ArrayList

@Entity
@Table(name = "kblog_page")
class Page(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_page", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        var title: String? = null,
        var layout: String? = null,
        var path: String = "/article",//模板路径(正文)
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
        @Column(name = "status", nullable = false, length = 1)
        var status: String = "A",
        @Column(nullable = false)
        var date: Date = Calendar.getInstance().time,
        var excerpt: String? = null,//文章的摘要
        var sort: Int = 1,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_id", nullable = false)
        var category: Category? = null,
        @XmlTransient
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinTable(name = "kblog_page_tag", joinColumns = [JoinColumn(name = "page_id")], inverseJoinColumns = [JoinColumn(name = "tag_id")])
        var tags: MutableSet<Tag> = mutableSetOf()
)