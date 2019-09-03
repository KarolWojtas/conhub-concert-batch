package com.karol.conhubconcertbatch

import com.karol.conhubconcertbatch.config.quartzBeans
import org.quartz.JobDetail
import org.quartz.Trigger
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import java.util.*

@SpringBootApplication
@EnableBatchProcessing
class ConhubConcertBatchApplication
@Value("\${batch.concertJobName}")
val concertJobName: String? = null
@Value("\${batch.startHour}")
val startHour: Int? = null
fun main(args: Array<String>) {
    SpringApplication(ConhubConcertBatchApplication::class.java).apply {
        addInitializers(beans(concertJobName), quartzBeans(concertJobName?:"concertJob", startHour?:0))
        run(*args)
    }
    @Bean
    fun schedulerFactoryBean(concertQuartzJobTrigger: Trigger, quartzProperties: Properties, concertQuartzJobDetail: JobDetail) = SchedulerFactoryBean().apply {
        setTriggers(concertQuartzJobTrigger)
        setJobDetails(concertQuartzJobDetail)
        setQuartzProperties(quartzProperties)
    }
}


