package sms.persona.aimod.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

/**
 * Builds a vCard for a conversation's native name + phone number and hands it
 * to the F-Droid OpenContacts app's importer, where the user confirms the
 * save. Nothing is ever written to OpenContacts silently: the import UI
 * always runs in their app.
 */
object ContactVCard {

    const val OPENCONTACTS_PACKAGE = "opencontacts.open.com.opencontacts"
    const val OPENCONTACTS_IMPORT_ACTIVITY =
        "opencontacts.open.com.opencontacts.activities.ImportVcardActivity"

    /** Matches OpenContacts' ImportVcardActivity intent filter (text/* on content URIs). */
    const val VCARD_MIME_TYPE = "text/vcard"

    /**
     * vCard 3.0 text escaping (RFC 2426 §5.8.4): backslash first, then
     * newline, comma and semicolon.
     */
    fun escape(value: String): String = buildString {
        for (c in value) {
            when (c) {
                '\\' -> append("\\\\")
                '\n' -> append("\\n")
                '\r' -> append("\\n")
                ',' -> append("\\,")
                ';' -> append("\\;")
                else -> append(c)
            }
        }
    }

    /** Minimal vCard 3.0: formatted name plus one cell number. */
    fun build(name: String, phone: String): String = buildString {
        val safeName = escape(name.ifBlank { phone })
        appendLine("BEGIN:VCARD")
        appendLine("VERSION:3.0")
        appendLine("FN:$safeName")
        appendLine("N:;$safeName;;;")
        appendLine("TEL;TYPE=CELL:${escape(phone)}")
        appendLine("END:VCARD")
    }

    fun fileNameFor(name: String): String {
        val base = name.ifBlank { "contact" }
            .replace(Regex("[^a-zA-Z0-9._+() -]"), "_")
            .trim()
            .take(60)
            .ifEmpty { "contact" }
        return "$base.vcf"
    }

    fun writeToCache(context: Context, vcard: String, fileName: String): File {
        val dir = File(context.cacheDir, "vcards").apply { mkdirs() }
        return File(dir, fileName).apply { writeText(vcard) }
    }

    /** Explicit VIEW intent for OpenContacts' importer; throws ActivityNotFoundException when it is not installed. */
    fun viewIntent(uri: Uri): Intent =
        Intent(Intent.ACTION_VIEW).apply {
            setClassName(OPENCONTACTS_PACKAGE, OPENCONTACTS_IMPORT_ACTIVITY)
            setDataAndType(uri, VCARD_MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
}
