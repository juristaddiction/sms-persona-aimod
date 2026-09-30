package sms.persona.aimod.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ConversationColorTest {

    private fun convo() = Conversation(
        id = 1L,
        address = "+15550001",
        name = "Name",
        snippet = "",
        timestamp = 1L,
        unreadCount = 0,
        isMe = false
    )

    @Test
    fun defaultColorIsZeroMeaningFollowAppAccent() {
        assertEquals(0, convo().color)
    }

    @Test
    fun colorSurvivesCopy() {
        val colored = convo().copy(color = 0xFF6D4AFF.toInt())
        assertEquals(0xFF6D4AFF.toInt(), colored.color)
    }

    @Test
    fun resettingColorToZeroRestoresFollowAppAccent() {
        val colored = convo().copy(color = 0xFF6D4AFF.toInt())
        assertEquals(0, colored.copy(color = 0).color)
    }
}
