package com.ailecarki.tv.domain

import com.ailecarki.tv.domain.engine.GameEngine
import com.ailecarki.tv.domain.engine.PartialAnswerComposer
import com.ailecarki.tv.domain.engine.WheelEngine
import com.ailecarki.tv.domain.model.GamePhase
import com.ailecarki.tv.domain.model.Puzzle
import com.ailecarki.tv.domain.model.SegmentType
import com.ailecarki.tv.domain.rules.GameRules
import com.ailecarki.tv.domain.rules.WheelConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEnginePolishTest {
    private val rules = GameRules()
    private val engine = GameEngine(rules, WheelEngine(WheelConfig.DEFAULT), Random(7))
    private val puzzles = listOf(Puzzle("p1", "TEST", "KAKAK"))
    private val jokerIndex = WheelConfig.DEFAULT.indexOfFirst { it.type == SegmentType.JOKER }

    private fun jokerSelectionState() = engine.resolveWheelResult(
        engine.newGame(listOf("HALA", "NİNE"), puzzles, startingPlayer = 0).copy(
            phase = GamePhase.WHEEL_RESULT,
            spinSegmentIndex = jokerIndex,
        )
    )

    @Test fun wheel_has_12_large_segments() {
        assertEquals(12, WheelConfig.DEFAULT.size)
    }

    @Test fun joker_scores_1000_per_occurrence() {
        val s = engine.chooseConsonant(jokerSelectionState(), 'K')
        assertEquals(3000, s.players[0].score)
        assertEquals(0, s.jokerAttemptsRemaining)
        assertEquals(GamePhase.LETTER_REVEAL, s.phase)
    }

    @Test fun joker_first_miss_grants_exactly_one_retry() {
        val first = engine.chooseConsonant(jokerSelectionState(), 'Z')
        assertEquals(0, first.currentPlayerIndex)
        assertEquals(1, first.jokerAttemptsRemaining)
        assertEquals(GamePhase.LETTER_SELECTION, first.phase)
        val second = engine.chooseConsonant(first, 'Y')
        assertEquals(1, second.currentPlayerIndex)
        assertEquals(0, second.jokerAttemptsRemaining)
        assertEquals(GamePhase.PLAYER_TURN, second.phase)
    }

    @Test fun joker_success_consumes_double_but_does_not_double_joker_score() {
        val start = jokerSelectionState().copy(doubleActive = true)
        val result = engine.chooseConsonant(start, 'K')
        assertEquals(3000, result.players[0].score)
        assertTrue(!result.doubleActive)
    }

    @Test fun player_count_supports_2_3_4_and_random_start_stays_in_range() {
        for (n in 2..4) {
            val names = (1..n).map { "OYUNCU$it" }
            val s = engine.newGame(names, puzzles, startingPlayer = null)
            assertEquals(n, s.players.size)
            assertTrue(s.currentPlayerIndex in 0 until n)
        }
    }

    @Test fun partial_answer_only_fills_hidden_tiles() {
        val answer = "İSTANBUL"
        val revealed = setOf('İ', 'S', 'A')
        val missing = PartialAnswerComposer.missingPositions(answer, revealed)
        val typed = missing.map { answer[it] }
        assertEquals(answer, PartialAnswerComposer.compose(answer, revealed, typed))
    }
}
