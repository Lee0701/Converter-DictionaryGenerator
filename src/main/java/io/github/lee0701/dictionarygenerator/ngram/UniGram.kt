package io.github.lee0701.dictionarygenerator.ngram

import java.io.DataOutputStream
import java.lang.StringBuilder
import java.nio.ByteBuffer

class UniGram(vocab: Map<String, Int>) {
    val indexToWord = vocab.keys.mapIndexed { i, s -> i to s }.toMap()
    val wordToIndex = vocab.keys.mapIndexed { i, s -> s to i }.toMap()
    val indexToFrequency = vocab.values.mapIndexed { i, f -> i to f }.toMap()

    fun encode(vararg words: String): List<Int> {
        return encode(words.toList())
    }

    fun encode(words: List<String>): List<Int> {
        return words.map { w -> wordToIndex[w] ?: -1 }
    }

    fun write(os: DataOutputStream) {
        os.writeInt(indexToWord.size)
        for(i in 0 until indexToWord.size) {
            os.writeChars(indexToWord[i] ?: "")
            os.writeShort(0)
            os.writeInt(indexToFrequency[i] ?: 0)
        }
    }

    companion object {
        fun read(bb: ByteBuffer): UniGram {
            val vocab = mutableMapOf<String, Int>()
            var p = 0
            val size = bb.getInt(p)
            p += 4
            for(i in 0 until size) {
                val text = getChars(bb, p)
                p += text.length*2 + 2
                val frequency = bb.getInt(p)
                p += 4
                vocab[text] = frequency
            }
            return UniGram(vocab.toMap())
        }

    }
}