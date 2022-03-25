package io.github.lee0701.dictionarygenerator

import java.io.DataOutputStream
import java.io.File

fun main() {
//    generateMeaningDictionary()
//    val dictionary = DiskDictionary(File("output/meaning.bin").inputStream())
//    println(dictionary.search("성"))
//    println(dictionary.search(" "))
//    println(dictionary.search("남을"))
//    println(dictionary.search("차"))

//    generateCombinationDictionary()
    val dictionary = DiskDictionary(File("output/combination.bin").inputStream())
    println(dictionary.search("금흠").map { it.result })
    println(dictionary.search("목자").map { it.result })
    println(dictionary.search("저자").map { it.result })
    println(dictionary.search("구남").map { it.result })
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

fun generateCombinationDictionary() {
    val dictionary = Dictionary()
    val freqHanja = {}.javaClass.getResourceAsStream("/freq-hanja.txt").bufferedReader().readLines()
        .map { it.split(":") }.map { it[0] to it[1].toInt() }.toMap()
    val comment = mutableListOf<String>()
    val idc = "⿰⿱⿲⿳⿴⿵⿶⿷⿸⿹⿺⿻".toList()
    val ids = {}.javaClass.getResourceAsStream("/ids.txt").bufferedReader().readLines()
        .filter { !it.startsWith('#') }
        .map { it.split('\t') }
        .filter { it.size == 3 }
        .associate { (_, c, s) -> c to s }
    val readings = {}.javaClass.getResourceAsStream("/hanja.txt").bufferedReader().readLines()
        .filter { !it.startsWith('#') }
        .map { it.split(':') }
        .filter { it.size == 3 }
        .filter { (r) -> r.length == 1 }
        .groupBy { it[1] }
        .map { (c, e) -> c to e.map { (r) -> r } }
        .toMap()
//    println(readings)
    val list = {}.javaClass.getResourceAsStream("/ids.txt").bufferedReader().readLines()
        .filter { !it.startsWith('#') }
        .map { it.split('\t') }
        .filter { it.size == 3 }
        .map { (_, c) -> c }
    for(c in list) {
        val key = ids[c]?.let { it.filter { c -> c !in idc } } ?: continue
        if(key.length == 1) continue
        val readingKeys = getReadingKey(key, readings)
        val result = c
        val frequency = freqHanja[result] ?: 0
        readingKeys.forEach { k ->
            dictionary.insert(k, Dictionary.Entry(result, "", frequency))
        }
    }
    val outputDir = File("output")
    outputDir.mkdirs()
    val dos = DataOutputStream(File(outputDir, "combination.bin").outputStream())
    dos.write((comment.joinToString("\n") + 0.toChar()).toByteArray())
    dictionary.write(dos)
}

fun getReadingKey(key: String, readings: Map<String, List<String>>): List<String> {
    val mapped = key.map { readings[it.toString()] ?: emptyList() }
    if(mapped.contains(emptyList())) return emptyList()
    fun getRecursive(mapped: List<List<String>>, index: Int): List<String> {
        if(index == mapped.size - 1) return mapped[index]
        else return mapped[index].flatMap { a -> getRecursive(mapped, index + 1).map { b -> a + b } }
    }
    return getRecursive(mapped, 0)
}
