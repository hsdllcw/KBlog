package io.kblog.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_tag")
class Tag(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_tag", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        @Column(unique = true)
        var name: String? = null,
        var sign: String? = null,
        var sort: Int = 1,

        @XmlTransient
        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "kblog_page_tag", joinColumns = [JoinColumn(name = "tag_id")], inverseJoinColumns = [JoinColumn(name = "page_id")])
        var pages: MutableSet<Page> = mutableSetOf()
)