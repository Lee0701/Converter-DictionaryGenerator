package io.github.lee0701.dictionarygenerator.ngram

import java.io.DataOutputStream

class NGram(val uniGram: UniGram) {

    val root = Node()

    fun insert(key: List<Int>, entry: Entry) {
        var p = root
        for(c in key.reversed()) {
            p = p.children.getOrPut(c) { Node() }
        }
        p.entries += entry
    }

    fun search(key: List<Int>): List<Result> {
        var p = root
        for(c in key.reversed()) {
            p = p.children[c] ?: return listOf()
        }
        return p.entries.mapNotNull { uniGram.indexToWord[it.result]?.let { text -> Result(text, it.frequency) } }
    }

    fun write(os: DataOutputStream) {
        uniGram.write(os)
        val rootAddress = root.write(os)
        os.writeInt(rootAddress)
    }

    data class Node(
        val children: MutableMap<Int, Node> = mutableMapOf(),
        val entries: MutableList<Entry> = mutableListOf(),
    ) {
        fun write(os: DataOutputStream): Int {
            val childrenMap = children.mapValues { (c, node) ->
                node.write(os)
            }
            val start = os.size()
            os.writeInt(children.size)
            childrenMap.forEach { (c, address) ->
                os.writeInt(c)
                os.writeInt(address)
            }
            os.writeInt(entries.size)
            entries.forEach { entry ->
                entry.write(os)
            }
            return start
        }
    }

    data class Entry(
        val result: Int,
        val frequency: Int,
    ) {
        fun write(os: DataOutputStream) {
            os.writeInt(result)
            os.writeInt(frequency)
        }
    }

    data class Result(
        val result: String,
        val frequency: Int,
    )

}