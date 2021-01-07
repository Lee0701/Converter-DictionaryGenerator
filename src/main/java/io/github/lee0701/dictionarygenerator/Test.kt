package io.github.lee0701.dictionarygenerator

import java.io.File

fun main() {
    val dictionary = DiskDictionary(File("dict.bin").inputStream())
    val result = dictionary.search("한")
    println(result)
}
