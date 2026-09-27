package com.ailecarki.tv.domain.rules

import com.ailecarki.tv.domain.model.SegmentType
import com.ailecarki.tv.domain.model.WheelSegment

/** Çark dilimleri buradan düzenlenir. Sıra, çarkta saat yönündeki sıradır (0 = başlangıçta en üstte). */
object WheelConfig {
    const val LABEL_BANKRUPT = "İFLAS"
    const val LABEL_LOSE_TURN = "SIRA GEÇ"
    const val LABEL_DOUBLE = "2X"
    const val LABEL_JOKER = "JOKER"

    private fun p(value: Int, color: Int) = WheelSegment(SegmentType.POINTS, value, value.toString(), color)
    private val bankrupt = WheelSegment(SegmentType.BANKRUPT, 0, LABEL_BANKRUPT, 8)
    private val loseTurn = WheelSegment(SegmentType.LOSE_TURN, 0, LABEL_LOSE_TURN, 9)
    private val double = WheelSegment(SegmentType.DOUBLE, 0, LABEL_DOUBLE, 10)
    private val joker = WheelSegment(SegmentType.JOKER, 0, LABEL_JOKER, 11)

    /**
     * 12 büyük dilim (her biri 30°) — TV'den uzaktan okunur. Özel dilimler eşit aralıklı (her 3 dilimde bir).
     * Saat yönünde: 100 · 500 · 2X · 300 · 1000 · JOKER · 400 · 750 · İFLAS · 250 · 2000 · SIRA GEÇ
     */
    val DEFAULT: List<WheelSegment> = listOf(
        p(100, 0), p(500, 2), double,
        p(300, 3), p(1000, 1), joker,
        p(400, 4), p(750, 0), bankrupt,
        p(250, 5), p(2000, 3), loseTurn,
    )
}
