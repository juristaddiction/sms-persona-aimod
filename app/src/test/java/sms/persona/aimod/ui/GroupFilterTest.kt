package sms.persona.aimod.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import sms.persona.aimod.data.Conversation

class GroupFilterTest {

    private fun convo(id: Long, archived: Boolean = false, blocked: Boolean = false) =
        Conversation(
            id = id,
            address = "+1555123$id",
            name = "Name $id",
            snippet = "",
            timestamp = id,
            unreadCount = 0,
            isMe = false,
            archived = archived,
            blocked = blocked
        )

    @Test
    fun noFilterShowsInboxOnly() {
        val list = listOf(convo(1), convo(2, archived = true), convo(3, blocked = true))
        assertEquals(listOf(1L), applyArchiveAndGroupFilter(list, false, null, emptySet()).map { it.id })
    }

    @Test
    fun archivedViewIgnoresGroupFilter() {
        val list = listOf(convo(1, archived = true), convo(2, archived = true))
        assertEquals(
            listOf(1L, 2L),
            applyArchiveAndGroupFilter(list, true, 7L, setOf(2L)).map { it.id }
        )
    }

    @Test
    fun groupFilterKeepsOnlyMembers() {
        val list = listOf(convo(1), convo(2), convo(3))
        assertEquals(
            listOf(2L),
            applyArchiveAndGroupFilter(list, false, 7L, setOf(2L)).map { it.id }
        )
    }

    @Test
    fun groupFilterWithNoMembersShowsEmpty() {
        val list = listOf(convo(1), convo(2))
        assertEquals(
            emptyList<Long>(),
            applyArchiveAndGroupFilter(list, false, 7L, emptySet()).map { it.id }
        )
    }

    @Test
    fun blockedConversationsNeverShown() {
        val list = listOf(convo(1, blocked = true), convo(2, archived = true, blocked = true))
        assertEquals(
            emptyList<Long>(),
            applyArchiveAndGroupFilter(list, false, null, emptySet()).map { it.id }
        )
        assertEquals(
            emptyList<Long>(),
            applyArchiveAndGroupFilter(list, true, null, emptySet()).map { it.id }
        )
    }
}
