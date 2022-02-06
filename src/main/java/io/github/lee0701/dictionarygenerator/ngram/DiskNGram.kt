package io.github.lee0701.dictionarygenerator.ngram

import java.io.InputStream
import java.nio.ByteBuffer

class DiskNGram(input: InputStream) {
    private val data = ByteBuffer.wrap(input.readAllBytes())
    val uniGram = UniGram.read(data)

    fun search(key: List<Int>): List<NGram.Result> {
        // root
        var p = data.getInt(data.capacity() - 4)
        for(c in key.reversed()) {
            // children count
            val childrenCount = data.getInt(p)
            for(i in 0 until childrenCount) {
                val ch = data.getInt(p + 4 + i*8)
                val addr = data.getInt(p + 4 + i*8 + 4)
                if(ch == c) {
                    p = addr
                    break
                } else if(i == childrenCount - 1) {
                    return listOf()
                }
            }
        }
        val childrenCount = data.getInt(p)
        p += 4 + childrenCount*8
        val entryCount = data.getInt(p)
        p += 4
//        println(p.toString(16))
        val entries = (0 until entryCount).map {
            val result = data.getInt(p)
            p += 4
            val frequency = data.getInt(p)
            p += 4
            NGram.Entry(result, frequency)
        }
        return entries.mapNotNull { uniGram.indexToWord[it.result]?.let { text -> NGram.Result(text, it.frequency) } }
    }

}