package com.nhenia.widgtit

import org.junit.Assert.assertTrue
import org.junit.Test

class HintWidgetProviderTest {

    @Test
    fun testGetRandomHint() {
        val hints = arrayOf("Hint 1", "Hint 2", "Hint 3")
        val randomHint = HintWidgetProvider.getRandomHint(hints)
        assertTrue(hints.contains(randomHint))
    }
}
