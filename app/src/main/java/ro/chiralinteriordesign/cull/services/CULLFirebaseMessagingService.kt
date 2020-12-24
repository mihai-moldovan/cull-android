package ro.chiralinteriordesign.cull.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Preferences

/**
 * Created by Mihai Moldovan on 24/12/2020.
 */
class CULLFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CORE_TOPIC = "core"
    }

    override fun onNewToken(token: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(CORE_TOPIC)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.i("Firebase", "received push")
        val app = App.instance
        app.preferences.put(Preferences.Key.CACHE_TIMESTAMP, 0L)
    }
}