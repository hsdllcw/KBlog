package io.kblog.domain

import javax.persistence.*

@Entity
@Table(name = "kblog_collocated")
class Collocated(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_collocated", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        var name: String? = null,
        var sign: String? = null,
        var type: String? = null,
        var status: Boolean? = null,
        var treeCode: String? = null
)