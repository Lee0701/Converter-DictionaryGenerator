package io.github.lee0701.dictionarygenerator.ngram

class NGramPredictor(private val nGram: DiskNGram) {

    val weights = mapOf(
        0 to 0.15,
        1 to 0.3,
        2 to 0.5,
        3 to 0.06,
        4 to 0.004,
        5 to 0.0001,
    )

    fun predict(context: String): Map<String, Double> {
        val tokens = tokenize(context)
        val maxUniform = (nGram.uniGram.indexToFrequency.maxOfOrNull { it.value } ?: 1).toDouble()
        val uniforms = nGram.uniGram.indexToFrequency.map { nGram.uniGram.indexToWord[it.key]!! to (it.value / maxUniform * weights[0]!!) }.toMap()
        val compoundResult = uniforms.toMutableMap()
        (1 until 5).forEach { i ->
            val gram = tokens.takeLast(i)
            val result = nGram.search(gram).map { it.result to it.frequency }
            val maxFreq = (result.maxOfOrNull { (_, freq) -> freq } ?: 1).toDouble()
            println(maxFreq)
            val relative = result.map { (text, freq) -> text to (freq / maxFreq) }
            val weighted = relative.map { (text, freq) -> text to (freq * weights[gram.size]!!) }
            weighted.forEach { (text, freq) -> compoundResult[text] = (compoundResult[text] ?: 0.0) + freq }
            println("$gram ${weighted.take(10)}")
        }
        return compoundResult.toMap()
    }

    fun tokenize(text: String): List<Int> {
        val result = mutableListOf<Int>()
        text.indices.forEach { i ->
            if(result.sumOf { if(it == -1) 1 else nGram.uniGram.indexToWord[it]?.length ?: 1 } <= i) {
                for(j in (i .. text.length).reversed()) {
                    val substr = text.substring(i, j)
                    val index = nGram.uniGram.wordToIndex[substr]
                    if(index != null) {
                        result += index
                        break
                    } else if(substr.length == 1) {
                        result += -1
                    }
                }
            }
        }
        return result.filter { it != -1 }
    }
}