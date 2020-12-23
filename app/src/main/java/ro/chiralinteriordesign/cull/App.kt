package ro.chiralinteriordesign.cull

import retrofit2.Retrofit
import ro.chiralinteriordesign.cull.services.LocalRepository
import ro.chiralinteriordesign.cull.services.Webservice
import ro.chiralinteriordesign.cull.services.createWebservice


/**
 * Created by Mihai Moldovan on 20/12/2020.
 */
class App : android.app.Application() {

    val preferences: Preferences by lazy { Preferences(this) }

    val webservice: Webservice by lazy { createWebservice(this) }

    val localRepository: LocalRepository by lazy { LocalRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    companion object {

        @JvmStatic
        lateinit var instance: App
            private set
    }
}