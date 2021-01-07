package io.github.lee0701.dictionarygenerator

import java.io.DataOutputStream
import java.io.File

fun main() {
//    genetateTestDictionary()
    generateHanjaDictionary()
}

fun generateHanjaDictionary() {
    val dictionary = Dictionary()
    val reader = {}.javaClass.getResourceAsStream("/hanja.txt").bufferedReader()
    val comment = mutableListOf<String>()
    while(true) {
        val line = reader.readLine() ?: break
        if(line.isEmpty()) continue
        if(line[0] == '#') comment += line
        else {
            val items = line.split(':')
            val key = items[0]
            val result = items[1]
            val extra = items[2]
            dictionary.insert(key, Dictionary.Entry(result, extra, 0))
        }
    }
    val outputDir = File("output")
    outputDir.mkdirs()
    val dos = DataOutputStream(File(outputDir, "dict.bin").outputStream())
    dos.write(comment.joinToString("\n").toByteArray())
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
