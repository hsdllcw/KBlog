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
        var id: Int? = null,
        @ElementCollection
        @CollectionTable(name = "kblog_model_field_custom", joinColumns = [JoinColumn(name = "modefiel_id")])
        @MapKeyColumn(name = "f_key", length = 50)
        @Column(name = "f_value", length = 2000)
        var customs: Map<String, String> = HashMap(0),
        @XmlTransient
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "model_id", nullable = false)
        var model: Model? = null,
        var type: Int? = null,
        var innerType: Int? = null,
        var label: String? = null,
        var name: String? = null,
        var prompt: String? = null,
        var defValue: String? = null,
        var required: Boolean? = null,
        var disabled: Boolean? = null
)