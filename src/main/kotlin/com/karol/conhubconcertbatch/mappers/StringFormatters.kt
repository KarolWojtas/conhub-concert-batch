package com.karol.conhubconcertbatch.mappers

class StodolaStringFormatter{
    companion object {
        // example "koncert-135601-LESKI___OPEN_STAGE_.htm"
        fun format(exp: String): String = with(exp){
            val (prefix, name, suffix) = "(koncert-\\d+)-([\\p{L}\\W_'\\(\\)]+)(\\.htm)".toRegex(RegexOption.IGNORE_CASE).find(this)!!.destructured
            name.replace("_+".toRegex()," ").trim()
        }
    }
}