package com.karol.conhubconcertbatch.mappers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SMFormatter{
    companion object {

        fun format(exp: String): LocalDateTime = with(exp){
            val (date, text, time) = "(\\d{2}\\.\\d{2}\\.\\d{4}),(.+), (\\d{2}:\\d{2})".toRegex().find(this)!!.destructured
            LocalDateTime.parse("$date $time",  DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        }


    }
}
class ProtokulturaFormatter{
    // exmple: "16,LIS,2018,19:00"
    companion object {
        fun format(exp: String): LocalDateTime = with(exp){
            val (dayOfMonth, monthLiteral, year, time) = "(\\d+) (\\p{L}+) (\\d{4}),(\\d{2}:\\d{2})".toRegex(RegexOption.IGNORE_CASE).find(this)!!.destructured
            val month = PolishMonths.values().filter { it.id.contains(monthLiteral.toLowerCase()) }.map { it.number }.first()
            LocalDateTime.parse("${String.format("%02d", dayOfMonth.toInt())}/$month/$year/$time", DateTimeFormatter.ofPattern("dd/MM/yyyy/HH:mm"))
        }
    }
}
class StodolaFormatter{
    // example "13 grudnia 2018 19:00 (Czwartek) "
    companion object {
        fun format(exp:String): LocalDateTime = with(exp){
            val (dayOfMonth, monthLiteral, year, time, text) = "(\\d{1,2}) (\\p{L}+) (\\d{4}) (\\d{2}:\\d{2})(.+)" .toRegex(RegexOption.IGNORE_CASE).find(this)!!.destructured
            val month = PolishMonths.values().filter { monthLiteral.toLowerCase().contains(it.id)}.map { it.number }.first()
            LocalDateTime.parse("${String.format("%02d", dayOfMonth.toInt())}/$month/$year/$time", DateTimeFormatter.ofPattern("dd/MM/yyyy/HH:mm"))
        }
    }
}