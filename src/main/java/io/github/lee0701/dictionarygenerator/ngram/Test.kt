package io.github.lee0701.dictionarygenerator.ngram

import java.io.File

fun main() {
    val nGram = DiskNGram(File("output/ngram-100k.bin").inputStream())
//    testNGram(nGram)
    testPredictor(nGram)
}

fun testNGram(nGram: DiskNGram) {
    println(nGram.search(nGram.uniGram.encode("韓國", "의")).filter { it.frequency > 10 })
    println(nGram.search(nGram.uniGram.encode("의")).filter { it.frequency > 10 })
}

fun testPredictor(nGram: DiskNGram) {
    val predictor = NGramPredictor(nGram)
    val result = predictor.predict("科擧")
    println(listOf("試驗").map { result[it] })
}
