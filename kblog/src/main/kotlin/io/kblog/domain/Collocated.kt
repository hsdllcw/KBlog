package io.kblog.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "kblog_collocated")
class Collocated(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_collocated", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        override var id: Int? = null,
        var name: String? = null,
        var sign: String? = null,
        var path: String? = null,
        var type: String? = null,
        var enabled: Boolean = true,
        var icon: String = "star-on",
        var treeCode: String? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JsonBackReference
        var parent: Collocated? = null,
        @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
        @OrderBy(value = "treeCode asc, id asc")
        var children: MutableSet<Collocated> = mutableSetOf()
): Base