package com.nhenia.widgtit.data

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
class HintDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var hintDao: HintDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        hintDao = db.hintDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetAll() = runBlocking {
        val hint = Hint(text = "Test Hint")
        hintDao.insert(hint)
        val allHints = hintDao.getAll()
        assertEquals(1, allHints.size)
        assertEquals("Test Hint", allHints[0].text)
    }

    @Test
    fun testDelete() = runBlocking {
        val hint = Hint(id = 1, text = "Delete Me")
        hintDao.insert(hint)
        var allHints = hintDao.getAll()
        assertEquals(1, allHints.size)

        hintDao.delete(allHints[0])
        allHints = hintDao.getAll()
        assertTrue(allHints.isEmpty())
    }
}
