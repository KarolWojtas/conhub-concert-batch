package com.karol.conhubconcertbatch.batch

import com.karol.conhubconcertbatch.domain.Concert
import com.karol.conhubconcertbatch.domain.ConcertRepository
import com.karol.conhubconcertbatch.domain.Venue
import com.karol.conhubconcertbatch.domain.VenueRepository
import com.karol.conhubconcertbatch.service.JsoupService
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.AfterChunk
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.core.annotation.OnWriteError
import org.springframework.batch.core.configuration.annotation.BatchConfigurer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import java.lang.Exception
import java.time.LocalDateTime

class ConcertItemReader(val venueName: String, val dateFormatter: ((String)->LocalDateTime)? = null, val artistFormatter: ((String)-> String)? = null,
                        val attributeName: String? = null): ItemReader<Concert>{
    @Autowired
    lateinit var venueRepository: VenueRepository
    @Autowired
    lateinit var jsoupService: JsoupService
    lateinit var venues: List<Venue>
    lateinit var concerts: List<Concert>
    lateinit var iterator: Iterator<Concert>
    @BeforeStep
    fun init(){
        if(venueRepository.count() == 0L){
            val venues = listOf(Venue(name = "Blues Club"), Venue(name = "Stary Maneż"), Venue(name = "Ucho"), Venue(name = "Protokultura"),
                    Venue(name = "Stodoła"), Venue(name = "Palladium"), Venue(name = "Progresja"))
            venueRepository.saveAll(venues)
        }
        venues = venueRepository.findAll()
        this.concerts = jsoupService.init(venueName = venueName, venues = venues)
                .mapToConcerts(dateFormatter = dateFormatter, artistFormatter = artistFormatter, attributeName = attributeName)
        iterator = concerts.iterator()
    }
    override fun read(): Concert? {
        return if(iterator.hasNext()) iterator.next() else null
    }
}
class ConcertItemWriter : ItemWriter<Concert>{
    @Autowired
    lateinit var concertRepository: ConcertRepository

    override fun write(concerts: MutableList<out Concert>?) {
        concerts?.asSequence()?.onEach { println(it) }?.forEach { concertRepository.save(it) }
    }
    @OnWriteError
    fun handleError(exception: Exception, chunk: List<Any>){
        println(exception)
    }
}

