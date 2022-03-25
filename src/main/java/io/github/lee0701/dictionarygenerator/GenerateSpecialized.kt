package io.github.lee0701.dictionarygenerator

import java.io.DataOutputStream
import java.io.File

fun main() {
    generateMeaningDictionary()
    val dictionary = DiskDictionary(File("output/meaning.bin").inputStream())
    println(dictionary.search("성"))
    println(dictionary.search(" "))
    println(dictionary.search("남을"))
    println(dictionary.search("차"))
}

fun generateMeaningDictionary() {
    val dictionary = Dictionary()
    val hanja = {}.javaClass.getResourceAsStream("/hanja.txt").bufferedReader()
    val freqHanja = {}.javaClass.getResourceAsStream("/freq-hanja.txt").bufferedReader().readLines()
        .map { it.split(":") }.map { it[0] to it[1].toInt() }.toMap()
    val comment = mutableListOf<String>()
    while(true) {
        val line = hanja.readLine() ?: break
        if(line.isEmpty()) continue
        if(line[0] == '#') comment += line
        else {
            val items = line.split(':')
            if(items.size < 3) continue
            if(items[0].length > 1) continue
            if(items[2].isEmpty()) continue
            val keys = items[2].split(",").map {
                val index = it.lastIndexOf(' ')
                return@map it.slice(0 until index).replace(" ", "") to
                        (if(index == -1) "" else it.takeLast(1))
            }
            val result = items[1]
            val extra = items[2]
            val frequency = freqHanja[result] ?: 0
            keys.forEach { (meaning, reading) ->
                dictionary.insert(meaning, Dictionary.Entry(result, extra, frequency))
                if(reading.isNotEmpty()) dictionary.insert(meaning + reading, Dictionary.Entry(result, extra, frequency))
            }
        }
    }
    val outputDir = File("output")
    outputDir.mkdirs()
    val dos = DataOutputStream(File(outputDir, "meaning.bin").outputStream())
    dos.write((comment.joinToString("\n") + 0.toChar()).toByteArray())
    dictionary.write(dos)
}
