package io.kblog.domain

import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_model_field")
class ModelField(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_model_field", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        override var id: Int? = null,
        @ElementCollection
        @CollectionTable(name = "kblog_model_field_custom", joinColumns = [JoinColumn(name = "modefiel_id")])
        @MapKeyColumn(name = "mf_key", length = 50)
        @Column(name = "mf_value", length = 2000)
        override var customs: Map<String, String> = HashMap(0),
        @XmlTransient
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "model_id", nullable = false)
        override var model: Model? = null,
        override var type: Int? = null,
        override var innerType: Int? = null,
        override var label: String? = null,
        override var name: String? = null,
        override var prompt: String? = null,
        override var defValue: String? = null,
        override var required: Boolean? = null,
        override var disabled: Boolean? = null
): Base.ModelFieldVo()