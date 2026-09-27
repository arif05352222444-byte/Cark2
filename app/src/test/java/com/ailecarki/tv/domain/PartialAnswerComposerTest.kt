package com.ailecarki.tv.domain

import com.ailecarki.tv.domain.engine.PartialAnswerComposer
import org.junit.Assert.assertEquals
import org.junit.Test

class PartialAnswerComposerTest {
    @Test fun onlyClosedTilesAreRequested() {
        val missing = PartialAnswerComposer.missingPositions("İSTANBUL", setOf('İ', 'A', 'U'))
        assertEquals(listOf(1, 2, 4, 5, 7), missing)
    }

    @Test fun reconstructsFullAnswerFromMissingLetters() {
        val full = PartialAnswerComposer.compose("İSTANBUL", setOf('İ', 'A', 'U'), listOf('S', 'T', 'N', 'B', 'L'))
        assertEquals("İSTANBUL", full)
    }

    @Test fun spacesStayFixed() {
        val full = PartialAnswerComposer.compose("KUTUP AYISI", setOf('U', 'A', 'I'), listOf('K', 'T', 'P', 'Y', 'S'))
        assertEquals("KUTUP AYISI", full)
    }
}
