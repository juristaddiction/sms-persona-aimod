package sms.persona.aimod.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactVCardTest {

    @Test
    fun escapeHandlesBackslashCommaSemicolonAndNewline() {
        assertEquals("a\\\\b\\,c\\;d\\ne\\nf", ContactVCard.escape("a\\b,c;d\ne\rf"))
    }

    @Test
    fun escapeLeavesPlainTextAlone() {
        assertEquals("Jane Doe +15551234567", ContactVCard.escape("Jane Doe +15551234567"))
    }

    @Test
    fun buildProducesVCardStructure() {
        val vcard = ContactVCard.build("Jane Doe", "+15551234567")
        val lines = vcard.lines()
        assertEquals("BEGIN:VCARD", lines.first())
        assertTrue(lines.contains("VERSION:3.0"))
        assertTrue(lines.contains("FN:Jane Doe"))
        assertTrue(lines.contains("TEL;TYPE=CELL:+15551234567"))
        assertEquals("END:VCARD", lines.last { it.isNotBlank() })
    }

    @Test
    fun buildEscapesSpecialCharsInName() {
        val vcard = ContactVCard.build("Doe, Jane; Jr.", "+15551234567")
        assertTrue(vcard.lines().contains("FN:Doe\\, Jane\\; Jr."))
    }

    @Test
    fun buildFallsBackToPhoneWhenNameIsBlank() {
        val vcard = ContactVCard.build("", "+15551234567")
        assertTrue(vcard.lines().contains("FN:+15551234567"))
    }

    @Test
    fun fileNameForSanitizesAndAddsVcfExtension() {
        assertEquals("Jane_Doe.vcf", ContactVCard.fileNameFor("Jane/Doe"))
        assertEquals("contact.vcf", ContactVCard.fileNameFor(""))
    }

    @Test
    fun targetsOpenContactsImporter() {
        assertEquals("opencontacts.open.com.opencontacts", ContactVCard.OPENCONTACTS_PACKAGE)
        assertEquals(
            "opencontacts.open.com.opencontacts.activities.ImportVcardActivity",
            ContactVCard.OPENCONTACTS_IMPORT_ACTIVITY
        )
        assertEquals("text/vcard", ContactVCard.VCARD_MIME_TYPE)
    }
}
