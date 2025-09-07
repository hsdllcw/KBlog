package io.kblog.domain

import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_site")
class Site(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_site", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        override var id: Int? = null,
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
        override var name: String = "Kblog",
        override var sign: String? = null,
        override var domain: String = "localhost",
        override var templateTheme: String? = null,
        override var enabled: Boolean = true
) : Base.SiteVo()