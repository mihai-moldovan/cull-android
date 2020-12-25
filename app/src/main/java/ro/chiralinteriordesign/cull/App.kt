package ro.chiralinteriordesign.cull

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import ro.chiralinteriordesign.cull.services.DataRepository


/**
 * Created by Mihai Moldovan on 20/12/2020.
 */
class App : android.app.Application() {

    val preferences: Preferences by lazy { Preferences(this) }
    val dataRepository: DataRepository by lazy { DataRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
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