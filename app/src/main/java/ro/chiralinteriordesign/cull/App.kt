package ro.chiralinteriordesign.cull

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import ro.chiralinteriordesign.cull.services.DataRepository
import ro.chiralinteriordesign.cull.utils.Utils


/**
 * Created by Mihai Moldovan on 20/12/2020.
 */
class App : android.app.Application() {

    val preferences: Preferences by lazy { Preferences(this) }
    val dataRepository: DataRepository by lazy { DataRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val versionCode = Utils.appVersionCode
        if (preferences.getInt(Preferences.Key.LAST_VERSION, 0) != versionCode) {
            preferences.put(Preferences.Key.LAST_VERSION, versionCode)
            preferences.clear(Preferences.Key.CACHE_TIMESTAMP)
        }
    }

    val isInForeground: Boolean = {
        val appProcessInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        (appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }()

    companion object {

        @JvmStatic
        lateinit var instance: App
            private set
    }
}