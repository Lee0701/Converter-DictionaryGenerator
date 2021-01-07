package io.github.lee0701.dictionarygenerator

import java.io.File

fun main() {
    val dictionary = DiskDictionary(File("output/dict.bin").inputStream())
    println(dictionary.search("한"))
    println(dictionary.search("한자"))
    println(dictionary.search("한국"))
}
