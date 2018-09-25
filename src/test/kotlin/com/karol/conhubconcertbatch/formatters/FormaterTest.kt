package com.karol.conhubconcertbatch.formatters

import com.karol.conhubconcertbatch.mappers.ProtokulturaFormatter
import com.karol.conhubconcertbatch.mappers.SMFormatter
import com.karol.conhubconcertbatch.mappers.StodolaFormatter
import com.karol.conhubconcertbatch.mappers.StodolaStringFormatter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestReporter
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.function.Executable


class FormatterTest{

    @Test
    fun `SM formatter`(){
        val expression = "23.02.2019, sobota, 20:00"

        val parsedDate = SMFormatter.format(expression)

        assertAll("",
                Executable { assertEquals(23, parsedDate.dayOfMonth) }
        )
    }
    @Test
    fun `SM formatter 2`(){
        val expression = "23.02.2019, sobota, 20:00"
        val regex = "(\\d{2}\\.\\d{2}\\.\\d{4}),(.+), (\\d{2}:\\d{2})".toRegex()

        val (date, text, time) = regex.find(expression)!!.destructured
        println("$date $text $time")


    }
    @Test
    fun `protokultura formatter test`(reporter: TestReporter){
        val example = "16 LIS 2018,19:00"
        reporter.publishEntry("format",ProtokulturaFormatter.Companion.format(example).toString() )
    }
    @Test
    fun `stodola formatter test`(reporter: TestReporter){
        val example = "13 grudnia 2018 19:00 (Czwartek) "
        reporter.publishEntry("format", StodolaFormatter.format(example).toString())
    }
    @Test
    fun `stodola name formatter`(reporter: TestReporter){
        val example = "koncert-135601-LESKI___OPEN_STAGE_.htm"
        reporter.publishEntry("format", StodolaStringFormatter.format(example))
    }
}