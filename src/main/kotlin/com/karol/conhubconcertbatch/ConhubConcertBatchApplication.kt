package com.karol.conhubconcertbatch

import com.karol.conhubconcertbatch.domain.*
import com.karol.conhubconcertbatch.mappers.ProtokulturaFormatter
import com.karol.conhubconcertbatch.mappers.SMFormatter
import com.karol.conhubconcertbatch.mappers.StodolaFormatter
import com.karol.conhubconcertbatch.mappers.StodolaStringFormatter
import com.karol.conhubconcertbatch.service.JsoupService
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources


@SpringBootApplication
@EnableBatchProcessing
class ConhubConcertBatchApplication

fun main(args: Array<String>) {
    SpringApplication(ConhubConcertBatchApplication::class.java).apply {
        addInitializers(beans())
        run(*args)
    }
}
class Bootstrap : CommandLineRunner{
    @Autowired
    lateinit var jsoupService: JsoupService
    @Autowired
    lateinit var venueRepository: VenueRepository
    @Autowired
    lateinit var concertRepository: ConcertRepository
    @Autowired
    lateinit var propsContainer: JsoupMappingPropertiesContainer

    override fun run(vararg args: String?) {
        val venues = listOf(Venue(name = "Blues Club"), Venue(name = "Stary Maneż"), Venue(name = "Ucho"), Venue(name = "Protokultura"),
                Venue(name = "Stodoła"), Venue(name = "Palladium"), Venue(name = "Wytwórnia"))
        venueRepository.deleteAll()
        concertRepository.deleteAll()

        val savedVenues: List<Venue> = venueRepository.saveAll(venues)
        val bcConcerts = jsoupService.init("Blues Club", savedVenues).mapToConcerts()
        val uchoConcerts = jsoupService.init(venueName = "Ucho", venues = savedVenues).mapToConcerts(attributeName = "datetime")
        val smConcerts = jsoupService.init("Stary Maneż", savedVenues).mapToConcerts(SMFormatter.Companion::format)
        val protoConcerts = jsoupService.init("Protokultura", savedVenues).mapToConcerts(dateFormatter = ProtokulturaFormatter.Companion::format)
        val stodConcerts = jsoupService.init("Stodoła", savedVenues).mapToConcerts(dateFormatter = StodolaFormatter.Companion::format,
                artistFormatter = StodolaStringFormatter.Companion::format)
        val palladiumConcerts = jsoupService.init("Palladium", venues).mapToConcerts(attributeName = "datetime")
        val wytworniaConcerts = jsoupService.init("Wytwórnia", venues).mapToConcerts(attributeName = "datetime")
        jsoupService.init("Tama", venues).mapToConcerts().forEach { println(it) }
        concertRepository.saveAll(bcConcerts+uchoConcerts+smConcerts+protoConcerts+stodConcerts+palladiumConcerts+wytworniaConcerts)
        println(concertRepository.count())



    }
}
