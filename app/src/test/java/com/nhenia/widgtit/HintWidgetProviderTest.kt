package com.nhenia.widgtit

import org.junit.Assert.assertTrue
import org.junit.Test

class HintWidgetProviderTest {

    @Test
    fun testGetRandomHint() {
        val hints = listOf(Advice(text = "Hint 1"), Advice(text = "Hint 2"), Advice(text = "Hint 3"))
        val randomHint = HintWidgetProvider.getRandomHint(hints)
        assertTrue(hints.map { it.text }.contains(randomHint))
    }

    @Test
    fun testGetRandomHintEmpty() {
        val randomHint = HintWidgetProvider.getRandomHint(emptyList())
        assertTrue(randomHint == "No hints available")
    }
}
