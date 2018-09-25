package com.karol.conhubconcertbatch
import com.karol.conhubconcertbatch.domain.JsoupMappingPropertiesContainer
import com.karol.conhubconcertbatch.service.ConcertService
import com.karol.conhubconcertbatch.service.ConcertServiceImpl
import com.karol.conhubconcertbatch.service.JsoupService
import org.springframework.context.support.beans

fun beans() = beans{
    bean<Bootstrap>(name = "bootstrap")
    bean<ConcertService>(isPrimary = true){ConcertServiceImpl()}
    bean{JsoupMappingPropertiesContainer()}
    bean { JsoupService() }
}