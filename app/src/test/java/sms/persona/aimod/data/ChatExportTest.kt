package sms.persona.aimod.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatExportTest {

    private fun convo(id: Long, name: String, address: String) = Conversation(
        id = id,
        address = address,
        name = name,
        snippet = "",
        timestamp = id,
        unreadCount = 0,
        isMe = false
    )

    private fun msg(id: Long, convoId: Long, body: String, isMe: Boolean, ts: Long = id * 1000L) =
        Message(
            id = id,
            conversationId = convoId,
            body = body,
            timestamp = ts,
            isMe = isMe,
            status = "sent"
        )

    @Test
    fun displayNamePrefersNameWithNumber() {
        val c = convo(1, "Mom", "+15551234567")
        assertTrue(ChatExport.displayNameFor(c).contains("Mom"))
        assertTrue(ChatExport.displayNameFor(c).contains("+15551234567"))
    }

    @Test
    fun displayNameFallsBackToNumber() {
        val c = convo(1, "+15551234567", "+15551234567")
        assertTrue(ChatExport.displayNameFor(c) == "+15551234567")
    }

    @Test
    fun textExportHasHeaderAndBothSides() {
        val c = convo(1, "Mom", "+15551234567")
        val text = ChatExport.buildText(
            listOf(
                c to listOf(
                    msg(1, 1, "Hello", isMe = false),
                    msg(2, 1, "Hi Mom", isMe = true)
                )
            )
        )
        assertTrue(text.contains("Mom"))
        assertTrue(text.contains("Hello"))
        assertTrue(text.contains("Hi Mom"))
        assertTrue(text.contains("Me:"))
    }

    @Test
    fun textExportMarksMediaMessages() {
        val c = convo(1, "Mom", "+15551234567")
        val m = msg(1, 1, "", isMe = false).copy(mediaType = "image")
        val text = ChatExport.buildText(listOf(c to listOf(m)))
        assertTrue(text.contains("[image]"))
    }

    @Test
    fun fileNameIsSanitized() {
        val c = convo(1, "Mom/Dad: *stars*", "+1555")
        val name = ChatExport.fileNameFor(c, "txt")
        assertTrue(name.endsWith(".txt"))
        assertFalse(name.contains("/"))
        assertFalse(name.contains(":"))
        assertFalse(name.contains("*"))
    }

    @Test
    fun fileNameForAllHasFallback() {
        val name = ChatExport.fileNameFor(null, "pdf")
        assertTrue(name.endsWith(".pdf"))
        assertTrue(name.isNotBlank())
    }
}
