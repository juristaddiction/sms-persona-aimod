package sms.persona.aimod

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-arms the automatic backup alarm after a reboot (alarms don't survive one). */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AutoBackupScheduler.schedule(context)
        }
    }
}
