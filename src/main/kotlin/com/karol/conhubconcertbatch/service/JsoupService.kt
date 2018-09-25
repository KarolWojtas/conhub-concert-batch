package com.karol.conhubconcertbatch.service

import com.karol.conhubconcertbatch.domain.Concert
import com.karol.conhubconcertbatch.domain.JsoupMappingPropertiesContainer
import com.karol.conhubconcertbatch.domain.MappingPropertiesKt
import com.karol.conhubconcertbatch.domain.Venue
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import kotlin.properties.Delegates
import kotlin.streams.toList

class JsoupService{

    @Autowired
    lateinit var propsContainer: JsoupMappingPropertiesContainer
    lateinit var venueList: List<Venue>
    lateinit var props: MappingPropertiesKt
    fun init(venueName: String, venues: List<Venue>) = propsContainer.mappingPropertiesList.filter { it.name == venueName }.first().let {
        props = it
        venueList = venues
        this
    }
     fun getElements(): List<Element> = props.url?.asSequence()?.map { Jsoup.parse(URL(it), 10000).select(props.containerSelector)}
            ?.flatten()?.toList()?: emptyList()

     fun List<Element>.mapToConcert(dateFormatter: ((String)->LocalDateTime)?, attributeName: String?, artistFormatter: ((String)->String)?): List<Concert> =
       this.asSequence().map { Concert(id = null,
               name = artistFormatter?.invoke(selectArtist(it, props))?:selectArtist(it,props),
               venue = venueList.first { it.name == props.name },
               date = dateFormatter?.invoke(selectDateString(it, attributeName))
                       ?: LocalDateTime.parse(selectDateString(it, attributeName), DateTimeFormatter.ofPattern(props.datePattern)))}
               .toList()

     fun selectDateString(element: Element, attributeName: String?): String = element.let {el ->
            props.dateSelector?.asSequence()
                    ?.map {selector ->  if(attributeName == null) el.select(selector).first().text() else el.select(selector).first().attr(attributeName) }
                    ?.joinToString(separator = ",")?: throw RuntimeException("Date selector error")

    }
    fun selectArtist(el: Element, props: MappingPropertiesKt) = if(props.artistAttributeName == null)el.select(props.artistSelector).first().text() else el.select(props.artistSelector).attr(props.artistAttributeName)
    fun mapToConcerts(dateFormatter: ((String) -> LocalDateTime)? = null, attributeName: String? = null, artistFormatter: ((String)->String)? = null) = getElements()
            .mapToConcert(dateFormatter, attributeName, artistFormatter)
}



