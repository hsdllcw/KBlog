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
        override var id: Int? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "site_id", nullable = false)
        override var site: Site? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        override var user: User? = null,
        override var name: String? = null,
        override var dataId: Int? = null,
        override var description: String? = null,
        @Lob
        @Basic(fetch = FetchType.LAZY)
        override var text: String? = null,
        override var ip: String? = null,
        override var country: String? = null,
        override var area: String? = null,
        @Temporal(TemporalType.TIMESTAMP)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        override var time: Date? = null,
        override var type: Int? = null
) : Base.OperationLogVo()