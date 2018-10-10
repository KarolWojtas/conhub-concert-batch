package com.karol.conhubconcertbatch
import com.karol.conhubconcertbatch.batch.ConcertItemReader
import com.karol.conhubconcertbatch.batch.ConcertItemWriter
import com.karol.conhubconcertbatch.domain.Concert
import com.karol.conhubconcertbatch.domain.JsoupMappingPropertiesContainer
import com.karol.conhubconcertbatch.mappers.ProtokulturaFormatter
import com.karol.conhubconcertbatch.mappers.SMFormatter
import com.karol.conhubconcertbatch.mappers.StodolaFormatter
import com.karol.conhubconcertbatch.mappers.StodolaStringFormatter
import com.karol.conhubconcertbatch.service.ConcertService
import com.karol.conhubconcertbatch.service.ConcertServiceImpl
import com.karol.conhubconcertbatch.service.JsoupService
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.support.beans
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.net.ConnectException

fun beans(concertJobName: String?) = beans{
    bean<ConcertService>(isPrimary = true){ConcertServiceImpl()}
    bean{JsoupMappingPropertiesContainer()}
    bean { JsoupService() }
    bean<JobLauncher>(name = "asyncJobLauncher", isPrimary = false){SimpleJobLauncher().apply {
        setJobRepository(ref())
        setTaskExecutor(SimpleAsyncTaskExecutor())
        afterPropertiesSet()
    }}
    bean<ItemWriter<Concert>>(name ="concertItemWriter", isPrimary = true){ConcertItemWriter()}
    bean<ItemReader<Concert>>("uchoReader"){ConcertItemReader(venueName = "Ucho", attributeName = "datetime")}
    bean<ItemReader<Concert>>("bluesClubReader"){ConcertItemReader(venueName = "Blues Club")}
    bean<ItemReader<Concert>>("staryManezReader"){ConcertItemReader(venueName = "Stary Maneż", dateFormatter = SMFormatter.Companion::format)}
    bean<ItemReader<Concert>>("protokulturaReader"){ConcertItemReader(venueName = "Protokultura", dateFormatter = ProtokulturaFormatter.Companion::format)}
    bean<ItemReader<Concert>>("stodolaReader"){ConcertItemReader(venueName = "Stodoła", dateFormatter = StodolaFormatter.Companion::format,
            artistFormatter = StodolaStringFormatter.Companion::format)}
    bean<ItemReader<Concert>>("palladiumReader"){ConcertItemReader(venueName = "Palladium", attributeName = "datetime")}
    bean<ItemReader<Concert>>("progresjaReader"){ConcertItemReader(venueName = "Progresja", attributeName = "datetime")}
    bean<Step>("uchoStep"){ concertStep(itemWriter = ref(), itemReader = ref("uchoReader"), stepName = "uchoStep", steps = ref())}
    bean<Step>("bluesClubStep"){ concertStep(itemWriter = ref(), itemReader = ref("bluesClubReader"), stepName = "bluesClubStep", steps = ref())}
    bean<Step>("staryManezStep"){ concertStep(itemWriter = ref(), itemReader = ref("staryManezReader"), stepName = "staryManezStep", steps = ref())}
    bean<Step>("protokulturaStep"){ concertStep(itemWriter = ref(), itemReader = ref("protokulturaReader"), stepName = "protokulturaStep", steps = ref())}
    bean<Step>("stodolaStep"){ concertStep(itemWriter = ref(), itemReader = ref("stodolaReader"), stepName = "stodolaStep", steps = ref())}
    bean<Step>("palladiumStep"){ concertStep(itemWriter = ref(), itemReader = ref("palladiumReader"), stepName = "palladiumStep", steps = ref())}
    bean<Step>("progresjaStep"){ concertStep(itemWriter = ref(), itemReader = ref("progresjaReader"), stepName = "progresjaStep", steps = ref())}
    bean<Job>(concertJobName){(ref<JobBuilderFactory>().get("concertJob").start(ref<Step>("uchoStep")).next(ref<Step>("bluesClubStep"))
            .next(ref<Step>("staryManezStep")).next(ref<Step>("protokulturaStep")).next(ref<Step>("stodolaStep"))
            .next(ref<Step>("palladiumStep")).next(ref<Step>("progresjaStep")).build())}

}
fun concertStep(itemWriter: ItemWriter<Concert>, steps: StepBuilderFactory, itemReader: ItemReader<Concert>, stepName: String, chunkSize: Int = 5) = steps.get(stepName)
        .chunk<Concert, Concert>(chunkSize)
        .reader(itemReader)
        .writer(itemWriter)
        .faultTolerant()
        .skip(com.mongodb.MongoWriteException::class.java)
        .skip(org.springframework.dao.DuplicateKeyException::class.java)
        .noSkip(ConnectException::class.java)
        .skipLimit(Integer.MAX_VALUE)
        .allowStartIfComplete(true)
        .build()
