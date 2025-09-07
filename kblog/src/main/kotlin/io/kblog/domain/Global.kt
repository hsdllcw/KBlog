package io.kblog.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "kblog_global")
class Global(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_kblog_global", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        override var id: Int? = null,
        override var protocol: String = "http",
        override var port: Int = 80,
        override var version: String = "1.0.0-SNAPSHOT",
        @ElementCollection
        @CollectionTable(name = "kblog_global_custom", joinColumns = [JoinColumn(name = "global_id")])
        @MapKeyColumn(name = "g_key")
        @Column(name = "g_value")
        override var customs: Map<String, String?> = mutableMapOf()
) : Base.GlobalVo()