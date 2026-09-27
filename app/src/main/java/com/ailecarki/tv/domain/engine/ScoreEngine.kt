package com.ailecarki.tv.domain.engine

import com.ailecarki.tv.domain.model.Player
import com.ailecarki.tv.domain.rules.BankruptPolicy

object ScoreEngine {
    fun consonantPoints(wheelValue: Int, count: Int, multiplier: Int = 1): Int =
        wheelValue * count * multiplier

    /** Joker, 1000 puanlık dilim gibi harf adediyle çarpılır. */
    fun jokerPoints(count: Int, basePoints: Int): Int = if (count > 0) count * basePoints else 0

    fun canBuyVowel(score: Int, cost: Int): Boolean = score >= cost

    fun addPoints(player: Player, points: Int): Player =
        player.copy(score = player.score + points, roundScore = player.roundScore + points)

    fun applyBankrupt(player: Player, policy: BankruptPolicy): Player = when (policy) {
        BankruptPolicy.RESET_TOTAL -> player.copy(score = 0, roundScore = 0)
        BankruptPolicy.RESET_ROUND_EARNINGS ->
            player.copy(score = (player.score - player.roundScore).coerceAtLeast(0), roundScore = 0)
    }
}
