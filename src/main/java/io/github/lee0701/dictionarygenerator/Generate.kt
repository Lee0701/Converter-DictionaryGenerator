package io.github.lee0701.dictionarygenerator

import java.io.DataOutputStream
import java.io.File

fun main() {
//    genetateTestDictionary()
    generateHanjaDictionary()
    generateAdditionalDictionary("metro-station")
    generateAdditionalDictionary("newly-coined")
}

fun generateHanjaDictionary() {
    val dictionary = Dictionary()
    val hanja = {}.javaClass.getResourceAsStream("/hanja.txt").bufferedReader()
    val freqHanja = {}.javaClass.getResourceAsStream("/freq-hanja.txt").bufferedReader().readLines()
        .map { it.split(":") }.map { it[0] to it[1].toInt() }.toMap()
    val freqHanjaeo = {}.javaClass.getResourceAsStream("/freq-hanjaeo.txt").bufferedReader().readLines()
        .map { it.split(":") }.map { it[0] to it[1].toInt() }.toMap()
    val comment = mutableListOf<String>()
    while(true) {
        val line = hanja.readLine() ?: break
        if(line.isEmpty()) continue
        if(line[0] == '#') comment += line
        else {
            val items = line.split(':')
            if(items.size < 3) continue
            val key = items[0]
            val result = items[1]
            val extra = items[2]
            val frequency = freqHanja[result] ?: freqHanjaeo[result]?.let { it % 10000 } ?: 0
            if(frequency >= Short.MAX_VALUE) println("$result: $frequency > ${Short.MAX_VALUE}")
            dictionary.insert(key, Dictionary.Entry(result, extra, frequency))
        }
    }
    val outputDir = File("output")
    outputDir.mkdirs()
    val dos = DataOutputStream(File(outputDir, "dict.bin").outputStream())
    dos.write((comment.joinToString("\n") + 0.toChar()).toByteArray())
    dictionary.write(dos)
}

fun generateAdditionalDictionary(fileName: String) {
    val dictionary = Dictionary()
    val hanja = {}.javaClass.getResourceAsStream("/${fileName}.txt")?.bufferedReader() ?: return
    val comment = mutableListOf<String>()
    while(true) {
        val line = hanja.readLine() ?: break
        if(line.isEmpty()) continue
        if(line[0] == '#') comment += line
        else {
            val items = line.split(':')
            if(items.size < 3) continue
            val key = items[0]
            val result = items[1]
            val extra = items[2]
            val frequency = 0
            if(frequency >= Short.MAX_VALUE) println("$result: $frequency > ${Short.MAX_VALUE}")
            dictionary.insert(key, Dictionary.Entry(result, extra, frequency))
        }
    }
    val outputDir = File("output")
    outputDir.mkdirs()
    val dos = DataOutputStream(File(outputDir, "${fileName}.bin").outputStream())
    dos.write((comment.joinToString("\n") + 0.toChar()).toByteArray())
    dictionary.write(dos)
}

fun genetateTestDictionary() {
    val dictionary = Dictionary()
    dictionary.insert("한", Dictionary.Entry("漢", "한수", 100))
    dictionary.insert("한", Dictionary.Entry("韓", "한국", 100))
    dictionary.insert("한", Dictionary.Entry("汗", "땀", 50))
    dictionary.insert("자", Dictionary.Entry("字", "글자", 100))
    dictionary.insert("한자", Dictionary.Entry("漢字", "", 100))
    dictionary.insert("한자", Dictionary.Entry("韓字", "", 50))
    dictionary.insert("한식", Dictionary.Entry("韓食", "", 100))
    val outputDir = File("output")
    outputDir.mkdirs()
    dictionary.write(DataOutputStream(File(outputDir, "test.bin").outputStream()))
}
