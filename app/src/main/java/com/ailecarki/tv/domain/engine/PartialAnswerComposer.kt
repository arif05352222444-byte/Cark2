package com.ailecarki.tv.domain.engine

/**
 * Çözüm ekranında daha önce açılmış harfleri sabit tutup yalnızca kapalı kutuların
 * doldurulmasını sağlar. Böylece oyuncu cevabı baştan yazmak zorunda kalmaz.
 */
object PartialAnswerComposer {
    fun missingPositions(answer: String, revealed: Set<Char>): List<Int> =
        answer.indices.filter { index ->
            val c = answer[index]
            c.isLetterOrDigit() && c !in revealed
        }

    fun compose(answer: String, revealed: Set<Char>, entered: List<Char>): String {
        val missing = missingPositions(answer, revealed)
        require(entered.size == missing.size) {
            "Eksik harf sayısı ${missing.size}, girilen harf sayısı ${entered.size}"
        }
        val out = answer.toCharArray()
        missing.forEachIndexed { i, pos ->
            val up = TurkishAlphabet.upper(entered[i].toString())
            out[pos] = up.firstOrNull() ?: entered[i]
        }
        return String(out)
    }
}
