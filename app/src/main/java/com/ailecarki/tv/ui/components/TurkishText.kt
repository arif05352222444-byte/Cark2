package com.ailecarki.tv.ui.components

/**
 * Türkçe bulunma eki: ARİF → ARİF'TE, DEMİR → DEMİR'DE, BABA → BABA'DA, ANNE → ANNE'DE.
 * Ünlü uyumu (son ünlü) + sert ünsüz benzeşmesi (fıstıkçı şahap).
 */
fun locativeName(name: String): String {
    val up = name.trim()
    if (up.isEmpty()) return up
    val vowels = "AEIİOÖUÜaeıioöuü"
    val lastVowel = up.lastOrNull { it in vowels } ?: 'E'
    val front = lastVowel in "EİÖÜeiöü"
    val hard = up.last() in "FSTKÇŞHPfstkçşhp"
    val d = if (hard) 'T' else 'D'
    val v = if (front) 'E' else 'A'
    return "$up'$d$v"
}
