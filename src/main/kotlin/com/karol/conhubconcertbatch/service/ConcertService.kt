package com.karol.conhubconcertbatch.service

import com.karol.conhubconcertbatch.domain.Concert
import com.karol.conhubconcertbatch.domain.ConcertRepository
import org.springframework.beans.factory.annotation.Autowired

interface ConcertService{
    fun save(concert: Concert): Concert
}
class ConcertServiceImpl: ConcertService{
    @Autowired
    lateinit var concertRepository: ConcertRepository

    override fun save(concert: Concert): Concert = concertRepository.save(concert)
}