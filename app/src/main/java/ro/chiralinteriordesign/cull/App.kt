package ro.chiralinteriordesign.cull

/**
 * Created by Mihai Moldovan on 20/12/2020.
 */
class App : android.app.Application() {

    lateinit var preferences: Preferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = Preferences(this)
    }

    companion object {

        @JvmStatic
        lateinit var instance: App
            private set
    }
}