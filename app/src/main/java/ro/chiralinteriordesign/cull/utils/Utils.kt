package ro.chiralinteriordesign.cull.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import ro.chiralinteriordesign.cull.App
import java.util.*

/**
 * Created by Mihai Moldovan on 01/01/2021.
 */
class Utils {

    companion object {
        val appVersion: String? = try {
            val app: App = App.instance
            val pi: PackageInfo = app.packageManager.getPackageInfo(app.packageName, 0)
            String.format(Locale.US, "v %1\$s (%2\$d)", pi.versionName, pi.versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            //should never happen
            e.printStackTrace()
            "error"
        }


        val appVersionCode: Int =
            App.instance.packageManager.getPackageInfo(App.instance.packageName, 0).versionCode
    }
}