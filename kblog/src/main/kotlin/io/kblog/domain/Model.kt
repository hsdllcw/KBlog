package io.kblog.domain

import java.util.*
import javax.persistence.*
import javax.xml.bind.annotation.XmlTransient

@Entity
@Table(name = "kblog_model")
class Model(
        @Id
        @TableGenerator(name = "hibernate_sequences", pkColumnValue = "kblog_kblog_model", initialValue = 1, allocationSize = 10)
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequences")
        override var id: Int? = null,
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], mappedBy = "model")
        @OrderBy(value = "number asc, id asc")
        var fields: MutableList<ModelField> = ArrayList(0),
        @ElementCollection
        @CollectionTable(name = "kblog_model_custom", joinColumns = [JoinColumn(name = "model_id")])
        @MapKeyColumn(name = "m_key", length = 50)
        @Column(name = "m_value", length = 2000)
        override var customs: Map<String, String> = HashMap(0),
        @XmlTransient
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "site_id", nullable = false)
        var site: Site? = null,
        override var type: String? = null,
        override var name: String? = null,
        override var number: String? = null
) : Base.ModelVo()