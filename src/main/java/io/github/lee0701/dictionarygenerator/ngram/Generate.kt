package io.github.lee0701.dictionarygenerator.ngram

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStream

fun main() {
    val nGram = generateNGram()
    nGram.write(DataOutputStream(File("output/ngram-100k.bin").outputStream()))
}

fun readCorpus(): InputStream {
    return {}.javaClass.getResourceAsStream("/merged-unfiltered-donga-pp-t-medium-100k.txt")
}

fun makeCorpus(bufferedReader: BufferedReader, uniGram: UniGram): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    bufferedReader.forEachLine { line ->
        val tokens = line.split(" ").map { if(it == "_SP_") " " else it }
        result += uniGram.encode(tokens)
    }
    return result.toList()
}

fun generateNGram(): NGram {
    val uniGrams = mutableMapOf<String, Int>()
    readCorpus().bufferedReader().forEachLine { line ->
        val tokens = line.split(" ")
        tokens.forEach { token -> uniGrams[token] = (uniGrams[token] ?: 0) + 1 }
    }
    val uniGramFreqs = uniGrams.toSortedMap().entries.sortedByDescending { it.value }.associate { it.key to it.value }
    val filteredUniGram = uniGramFreqs.filterValues { it >= 10 }.mapKeys { (k, _) -> if(k == "_SP_") " " else k }

    println("${uniGramFreqs.size} -> ${filteredUniGram.size} words")

    val nGram = NGram(UniGram(filteredUniGram))

    val corpus = makeCorpus(readCorpus().bufferedReader(), nGram.uniGram)
    println("${corpus.size} lines")

    addNGrams(corpus, 2, nGram, 5)
    addNGrams(corpus, 3, nGram, 5)
    addNGrams(corpus, 4, nGram, 4)
    addNGrams(corpus, 5, nGram, 4)
//    println(nGram.search(nGram.uniGram.encode("韓國", "의")).filter { it.frequency > 10 })
//    println(nGram.search(nGram.uniGram.encode("의")).filter { it.frequency > 10 })
    return nGram
}

fun addNGrams(corpus: List<List<Int>>, n: Int, nGram: NGram, minFreq: Int = 0) {
    val result = mutableMapOf<List<Int>, Int>()
    corpus.forEach { tokens ->
        (0 until tokens.size - n + 1).forEach { i ->
            val seq = tokens.subList(i, i + n)
            result[seq] = (result[seq] ?: 0) + 1
        }
    }
    val filtered = result.filterValues { it >= minFreq }.filterKeys { !it.contains(-1) }
    val sorted = filtered.entries.sortedByDescending { it.value }.associate { it.key to it.value }
    println("$n: ${result.size} -> ${filtered.size}")
//    println(sorted.entries.take(5).map { (k, v) -> "${k.map { nGram.uniGram.indexToWord[it] }}=$v" })
    sorted.forEach { (k, f) -> nGram.insert(k.dropLast(1), NGram.Entry(k.last(), f)) }
}
