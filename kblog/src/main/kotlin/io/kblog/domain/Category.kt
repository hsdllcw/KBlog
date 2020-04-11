package io.kblog.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_category")
class Category(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_category", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        var name: String? = null,
        var sign: String? = null,
        var treeCode: String? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JsonBackReference
        val parent: Category? = null,
        @XmlTransient
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "category")
        var pages: MutableSet<Page> = mutableSetOf()
)