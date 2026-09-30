package sms.persona.aimod

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import sms.persona.aimod.data.PhoneNumberUtils
import sms.persona.aimod.data.Repository
import sms.persona.aimod.data.RetentionPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MessagesApplication : Application() {
    lateinit var repository: Repository
        private set

    override fun onCreate() {
        super.onCreate()
        sms.persona.aimod.crash.CrashReporter.install(this)
        PhoneNumberUtils.init(this)
        repository = Repository(this)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            repository.migrateParticipants()
            val s = repository.settings
            repository.purgeRetainedSuspend(
                buckets = RetentionPolicy.activeBuckets(
                    enabled = s.retentionEnabled,
                    trash = s.retentionTrash,
                    keywordMessages = s.retentionKeywordMessages,
                    blockedSenders = s.retentionBlockedSenders
                ),
                trashDays = s.retentionTrashDays,
                spamDays = s.retentionSpamDays
            )
            // skip until SMS access is granted; MainActivity re-imports then
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                repository.syncFromSystem()
            }
        }
    }
}
