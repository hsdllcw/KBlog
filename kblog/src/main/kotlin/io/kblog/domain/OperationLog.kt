package io.kblog.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "kblog_operation_log")
class OperationLog(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_operation_log", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        var id: Int? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "site_id", nullable = false)
        var site: Site? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        var user: User? = null,

        var name: String? = null,
        var dataId: Int? = null,
        var description: String? = null,
        @Lob
        @Basic(fetch = FetchType.LAZY)
        var text: String? = null,
        var ip: String? = null,
        var country: String? = null,
        var area: String? = null,
        @Temporal(TemporalType.TIMESTAMP)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        var time: Date? = null,
        var type: Int? = null
)