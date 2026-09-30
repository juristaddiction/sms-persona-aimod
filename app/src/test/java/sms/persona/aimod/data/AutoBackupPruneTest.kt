package sms.persona.aimod.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AutoBackupPruneTest {

    private fun backup(name: String, dateAdded: Long) = "content://media/$name" to dateAdded

    @Test
    fun keepsNewestFive() {
        val rows = (1..8).map { backup("b$it.enc", it.toLong()) }
        val pruned = selectBackupsToPrune(rows, 5)
        assertEquals(3, pruned.size)
        assertEquals(
            listOf("content://media/b1.enc", "content://media/b2.enc", "content://media/b3.enc"),
            pruned
        )
    }

    @Test
    fun fewerThanKeepPrunesNothing() {
        val rows = (1..3).map { backup("b$it.enc", it.toLong()) }
        assertEquals(emptyList<String>(), selectBackupsToPrune(rows, 5))
    }

    @Test
    fun unorderedInputStillKeepsNewest() {
        val rows = listOf(
            backup("old.enc", 1L),
            backup("new.enc", 100L),
            backup("mid.enc", 50L)
        )
        assertEquals(listOf("content://media/old.enc"), selectBackupsToPrune(rows, 2))
    }

    @Test
    fun zeroKeepPrunesAll() {
        val rows = (1..3).map { backup("b$it.enc", it.toLong()) }
        assertEquals(3, selectBackupsToPrune(rows, 0).size)
    }
}
