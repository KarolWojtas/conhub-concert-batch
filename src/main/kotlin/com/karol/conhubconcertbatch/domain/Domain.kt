package com.karol.conhubconcertbatch.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
@CompoundIndex(unique = true,  name = "name_date", def = "{'name' : 1, 'date': 1}")
data class Concert(
        @Id val id: String? = null,
        val name: String,
        @DBRef val venue: Venue,
        val date: LocalDateTime
)
@Document
data class Venue(@Id val id: String? = null,
                 val name: String,
                 @Transient
                 val avatar: ByteArray? = null)

class MappingPropertiesKt {
    var name: String? = null
    var url: List<String>? = null
    var containerSelector: String? = null
    var artistSelector: String? = null
    var artistAttributeName: String? = null
    var dateSelector: List<String>? = null
    var datePattern: String? = null

    override fun toString(): String {
        return "MappingPropertiesKt(name=$name, url=$url, containerSelector=$containerSelector, artistSelector=$artistSelector, dateSelector=$dateSelector, datePattern=$datePattern)"
    }
}