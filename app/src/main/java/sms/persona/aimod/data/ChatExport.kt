package sms.persona.aimod.data

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatExport {

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun displayNameFor(convo: Conversation): String =
        if (convo.name != convo.address) "${convo.name} (${convo.display})" else convo.display

    fun fileNameFor(convo: Conversation?, ext: String): String {
        val base = convo?.let { displayNameFor(it) } ?: "all-conversations"
        val safe = base.replace(Regex("[^a-zA-Z0-9._+() -]"), "_").trim().take(60).ifEmpty { "chat" }
        return "$safe.$ext"
    }

    private fun messageLine(convo: Conversation, m: Message): String {
        val sender = if (m.isMe) "Me" else convo.name
        val body = if (m.mediaType != "text" && m.mediaType.isNotBlank()) {
            "[${m.mediaType}] ${m.body}".trim()
        } else {
            m.body
        }
        return "[${dateFmt.format(Date(m.timestamp))}] $sender: $body"
    }

    fun buildText(chats: List<Pair<Conversation, List<Message>>>): String = buildString {
        appendLine("Exported ${dateFmt.format(Date())}")
        appendLine()
        for ((convo, messages) in chats) {
            appendLine("=== ${displayNameFor(convo)} ===")
            for (m in messages.sortedBy { it.timestamp }) {
                appendLine(messageLine(convo, m))
            }
            appendLine()
        }
    }

    fun buildPdf(context: Context, chats: List<Pair<Conversation, List<Message>>>): ByteArray {
        val attrs = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("export", "export", 300, 300))
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .build()
        val doc = PrintedPdfDocument(context, attrs)
        try {
            val titlePaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 15f
                color = Color.BLACK
            }
            val subPaint = Paint().apply {
                textSize = 11f
                color = Color.DKGRAY
            }
            val bodyPaint = Paint().apply {
                textSize = 11f
                color = Color.BLACK
            }
            val margin = 48f
            var pageNumber = 0
            var page: PdfDocument.Page = doc.startPage(pageNumber++)
            var y = margin + 20f

            fun newPage() {
                doc.finishPage(page)
                page = doc.startPage(pageNumber++)
                y = margin + 20f
            }

            fun drawWrapped(text: String, paint: Paint) {
                var rest = text
                val maxW = page.info.pageWidth - 2 * margin
                while (rest.isNotEmpty()) {
                    if (y + paint.textSize * 1.35f > page.info.pageHeight - margin) newPage()
                    val count = paint.breakText(rest, true, maxW, null)
                    page.canvas.drawText(rest.substring(0, count), margin, y, paint)
                    y += paint.textSize * 1.35f
                    rest = rest.substring(count).trimStart()
                }
            }

            drawWrapped("Exported ${dateFmt.format(Date())}", subPaint)
            y += 10f
            for ((convo, messages) in chats) {
                if (y + 60f > page.info.pageHeight - margin) newPage()
                drawWrapped(displayNameFor(convo), titlePaint)
                drawWrapped(
                    "${messages.size} messages",
                    subPaint
                )
                y += 6f
                for (m in messages.sortedBy { it.timestamp }) {
                    drawWrapped(messageLine(convo, m), bodyPaint)
                }
                y += 18f
            }
            doc.finishPage(page)
            val out = ByteArrayOutputStream()
            doc.writeTo(out)
            return out.toByteArray()
        } finally {
            doc.close()
        }
    }
}
