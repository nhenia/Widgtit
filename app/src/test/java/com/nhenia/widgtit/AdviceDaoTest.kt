package com.nhenia.widgtit

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AdviceDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var adviceDao: AdviceDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        adviceDao = db.adviceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetAll() = runBlocking {
        val advice = Advice(text = "Test Hint")
        adviceDao.insert(advice)
        val allAdvice = adviceDao.getAllAdvice()
        assertEquals(1, allAdvice.size)
        assertEquals("Test Hint", allAdvice[0].text)
    }

    @Test
    fun testDelete() = runBlocking {
        val advice = Advice(id = 1, text = "Delete Me")
        adviceDao.insert(advice)
        var allAdvice = adviceDao.getAllAdvice()
        assertEquals(1, allAdvice.size)

        adviceDao.delete(allAdvice[0])
        allAdvice = adviceDao.getAllAdvice()
        assertTrue(allAdvice.isEmpty())
    }
}
