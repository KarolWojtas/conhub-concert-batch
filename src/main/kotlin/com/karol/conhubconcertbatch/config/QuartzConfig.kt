package com.karol.conhubconcertbatch.config

import org.quartz.*
import org.quartz.CronScheduleBuilder.*
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobLocator
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.context.support.beans
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.QuartzJobBean
import java.time.Instant
import java.util.*

class ConcertQuartzJob : QuartzJobBean(){
    lateinit var jobName: String
    lateinit var jobLauncher: JobLauncher
    lateinit var jobLocator: JobLocator

    override fun executeInternal(context: JobExecutionContext) {
        try {
            val job = jobLocator.getJob(jobName)
            val params = JobParametersBuilder().addString("JobId", Instant.now().toString()).toJobParameters()
            jobLauncher.run(job, params)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

}
fun quartzBeans(concertJobName: String, startHour: Int) = beans {
    bean { JobRegistryBeanPostProcessor().apply {
        setJobRegistry(ref())
    } }
    bean<JobDetail>("concertQuartzJobDetail"){
        val jobDataMap = JobDataMap().apply {
            put("jobName", concertJobName)
            put("jobLauncher", ref<JobLauncher>(name= "jobLauncher"))
            put("jobLocator", ref<JobLocator>())
        }
        JobBuilder.newJob(ConcertQuartzJob::class.java).withIdentity("concertQuartzJob")
                .setJobData(jobDataMap).storeDurably().build()
    }
    bean<Trigger>("concertQuartzJobTrigger"){
        TriggerBuilder.newTrigger().withIdentity("concertQuartzJobTrigger")
                .forJob(ref<JobDetail>("concertQuartzJobDetail"))
                .withSchedule(cronSchedule("0 0 0/12 * * *"))
                .withSchedule(dailyAtHourAndMinute(startHour,0))
                .build()
    }
    bean("quartzProperties"){
        PropertiesFactoryBean().apply {
            setLocation(ClassPathResource("/quartz.yml"))
            afterPropertiesSet()
        }.`object` ?: Properties()
    }
}