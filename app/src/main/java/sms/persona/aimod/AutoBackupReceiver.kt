package sms.persona.aimod

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Fires the automatic backup off the main thread. */
class AutoBackupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AutoBackupScheduler.ACTION) return
        val pending = goAsync()
        Thread {
            try {
                val app = context.applicationContext as MessagesApplication
                if (app.repository.settings.autoBackupEnabled) {
                    app.repository.autoBackupDatabase(context.applicationContext)
                }
            } catch (_: Exception) {
            } finally {
                pending.finish()
            }
        }.start()
    }
}
