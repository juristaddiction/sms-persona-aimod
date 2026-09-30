package sms.persona.aimod

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/** Schedules the automatic local backup alarm. Framework AlarmManager only —
 *  no extra dependencies, fully offline. */
object AutoBackupScheduler {
    const val ACTION = "sms.persona.aimod.AUTO_BACKUP"
    private const val REQUEST_CODE = 4101

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context.applicationContext, AutoBackupReceiver::class.java)
            .setAction(ACTION)
        return PendingIntent.getBroadcast(
            context.applicationContext,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(context: Context) {
        val appCtx = context.applicationContext
        cancel(appCtx)
        val repo = (appCtx as MessagesApplication).repository
        if (!repo.settings.autoBackupEnabled) return
        val manager = appCtx.getSystemService(AlarmManager::class.java) ?: return
        val days = repo.settings.autoBackupIntervalDays.coerceIn(1, 30)
        val interval = days * AlarmManager.INTERVAL_DAY
        manager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            interval,
            pendingIntent(appCtx)
        )
    }

    fun cancel(context: Context) {
        val appCtx = context.applicationContext
        val manager = appCtx.getSystemService(AlarmManager::class.java) ?: return
        val pi = pendingIntent(appCtx)
        manager.cancel(pi)
        pi.cancel()
    }
}
