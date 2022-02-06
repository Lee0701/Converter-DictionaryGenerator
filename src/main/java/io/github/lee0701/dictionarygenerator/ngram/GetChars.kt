package io.github.lee0701.dictionarygenerator.ngram

import java.lang.StringBuilder
import java.nio.ByteBuffer

fun getChars(bb: ByteBuffer, idx: Int): String {
    val sb = StringBuilder()
    var i = 0
    while(true) {
        val c = bb.getChar(idx + i)
        if(c.toInt() == 0) break
        sb.append(c)
        i += 2
    }
    return sb.toString()
}
