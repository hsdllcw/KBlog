package io.kblog.domain

import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_site")
class Site(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_site", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        @XmlTransient
        @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
        var children: List<Site>? = arrayListOf(),
        @XmlTransient
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "global_id", nullable = false)
        var global: Global? = null,
        @XmlTransient
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        var parent: Site? = null,
        var name: String? = null,
        var number: String? = null,
        var domain: String? = null,
        var templateTheme: String? = null,
        var status: Boolean? = null
)